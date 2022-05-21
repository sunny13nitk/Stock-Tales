package stocktales.DataLake.srv.impl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBeanBuilder;

import stocktales.DataLake.model.entity.DL_ScripPriceATH;
import stocktales.DataLake.model.pojo.CSVScPrices;
import stocktales.DataLake.model.repo.RepoATHScripPrices;
import stocktales.DataLake.model.repo.intf.IDates;

@Service("ATHUploadSrv")
public class DL_ScripPricesUploadATHSrv implements stocktales.DataLake.srv.intf.DL_ScripPricesUploadSrv
{

	@Autowired
	private RepoATHScripPrices repoSCPrices;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public boolean UploadScripPrices(MultipartFile file) throws Exception
	{
		boolean uploaded = false;
		String scCode = null;

		if (file != null)
		{
			Reader reader = new InputStreamReader(file.getInputStream());
			scCode = getScripCode(file.getOriginalFilename());
			if (StringUtils.hasText(scCode) && reader != null)
			{

				@SuppressWarnings("rawtypes")
				List<CSVScPrices> scPricesList = new CsvToBeanBuilder(reader).withSkipLines(1)
						.withType(CSVScPrices.class).build().parse();
				if (scPricesList != null)
				{
					if (scPricesList.size() > 0)
					{
						// Check if tABLE iS bLANK
						if (repoSCPrices.count() > 0)
						{

							/*
							 * cHECK IF sCRIP IS mAINTIANED in Table- IF so get Date Range maintained
							 */

							try
							{
								IDates dates4ScripMain = repoSCPrices.getMaxMinDatesforScrip(scCode);
								if (dates4ScripMain.getMaxdate() != null && dates4ScripMain.getMindate() != null)
								{
									savetoDB(scCode, scPricesList, dates4ScripMain);
								} else
								{
									// Scrip Not maintained - Persist as is
									savetoDB(scCode, scPricesList, null);
								}
							} catch (Exception e)
							{
								// Scrip Not maintained - Persist as is
								savetoDB(scCode, scPricesList, null);
							}

						} else // bLANK sC pRICES tABLE
						{
							savetoDB(scCode, scPricesList, null); // Persist to DB the Whole CSV
						}
					}
				}

			}
		}

		return uploaded;
	}

	private String getScripCode(String name)
	{
		String scCode = null;
		if (StringUtils.hasText(name))
		{
			String[] names = name.split("\\.");
			if (names.length > 0)
			{
				scCode = names[0];
			}
		}
		return scCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public boolean RefreshAndUploadScripPrices(MultipartFile file) throws Exception
	{
		boolean uploaded = false;
		String scCode = null;

		if (file != null)
		{
			Reader reader = new InputStreamReader(file.getInputStream());
			scCode = getScripCode(file.getOriginalFilename());
			if (StringUtils.hasText(scCode) && reader != null)
			{

				@SuppressWarnings("rawtypes")
				List<CSVScPrices> scPricesList = new CsvToBeanBuilder(reader).withSkipLines(1)
						.withType(CSVScPrices.class).build().parse();
				if (scPricesList != null)
				{
					if (scPricesList.size() > 0)
					{
						long numEntries;
						// Check if tABLE iS bLANK
						if (repoSCPrices.count() > 0)
						{

							/*
							 * cHECK IF sCRIP IS mAINTIANED in Table- IF so get Date Range maintained
							 */

							try
							{
								numEntries = repoSCPrices.getNumberofEntries4Scrip(scCode);
								if (numEntries > 0)
								{
									refreshandSavetoDB(scCode, scPricesList);
								} else
								{
									savetoDB(scCode, scPricesList, null);
								}
							} catch (Exception e)
							{
								// Scrip Not maintained - Persist as is
								savetoDB(scCode, scPricesList, null);
							}

						} else // bLANK sC pRICES tABLE
						{
							savetoDB(scCode, scPricesList, null); // Persist to DB the Whole CSV
						}
					}
				}

			}
		}

		return uploaded;
	}

	@Transactional
	private void refreshandSavetoDB(String scCode, List<CSVScPrices> scPricesList)
	{
		if (scPricesList != null && StringUtils.hasText(scCode))
		{
			if (scPricesList.size() > 0)
			{
				repoSCPrices.delete4SCrip(scCode);

				savetoDB(scCode, scPricesList, null);
			}
		}
	}

	@Transactional
	private void savetoDB(String scCode, List<CSVScPrices> csvList, IDates daterangeExcl)
	{
		if (csvList != null && StringUtils.hasText(scCode))
		{
			if (csvList.size() > 0)
			{

				List<DL_ScripPriceATH> scPrices = new ArrayList<DL_ScripPriceATH>();

				if (daterangeExcl == null)
				{
					// Create the Collection & persist thereafter at once
					for (CSVScPrices csvScPrices : csvList)
					{
						DL_ScripPriceATH scPrice = new DL_ScripPriceATH();
						scPrice.setSccode(scCode);
						scPrice.setDate(csvScPrices.getDate());
						scPrice.setCloseprice(csvScPrices.getAdjclose());
						scPrices.add(scPrice);
					}
					repoSCPrices.saveAll(scPrices);
				} else
				{
					/*
					 * Parse CSV to exclude the date range already maintained {Before/after Both}
					 */
					List<CSVScPrices> csvExclDateRange = csvList.stream().filter(

							x ->
							{
								if (x.getDate().before(daterangeExcl.getMindate())
										|| x.getDate().after(daterangeExcl.getMaxdate())

							)
								{
									return true;
								}
								return false;
							}).collect(Collectors.toList());

					if (csvExclDateRange != null)
					{
						if (csvExclDateRange.size() > 0)
						{
							for (CSVScPrices csvScPrices : csvExclDateRange)
							{
								DL_ScripPriceATH scPrice = new DL_ScripPriceATH();
								scPrice.setSccode(scCode);
								scPrice.setDate(csvScPrices.getDate());
								scPrice.setCloseprice(csvScPrices.getAdjclose());
								scPrices.add(scPrice);
							}
							repoSCPrices.saveAll(scPrices);
						}
					}

				}
			}
		}
	}

}
