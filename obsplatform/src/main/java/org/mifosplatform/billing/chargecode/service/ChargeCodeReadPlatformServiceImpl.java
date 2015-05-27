package org.mifosplatform.billing.chargecode.service;

import java.math.BigDecimal;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.chargecode.data.BillFrequencyCodeData;
import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.chargecode.data.ChargeTypeData;
import org.mifosplatform.billing.chargecode.data.DurationTypeData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 * 
 */
@Service
public class ChargeCodeReadPlatformServiceImpl implements
		ChargeCodeReadPlatformService {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ChargeCodeReadPlatformServiceImpl(
			final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #retrieveAllChargeCodes()
	 */
	public List<ChargeCodeData> retrieveAllChargeCodes() {

		final ChargeCodeMapper mapper = new ChargeCodeMapper();

		final String sql = "Select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class ChargeCodeMapper implements
			RowMapper<ChargeCodeData> {

		public String schema() {
			return "id as id, charge_code as chargeCode, charge_description as chargeDescription, charge_type as chargeType,"
					+ "charge_duration as chargeDuration, duration_type as durationType, tax_inclusive as taxInclusive,"
					+ "billfrequency_code as billFrequencyCode from b_charge_codes";
		}

		@Override
		public ChargeCodeData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String chargeCode = rs.getString("chargeCode");
			final String chargeDescription = rs.getString("chargeDescription");
			final String chargeType = rs.getString("chargeType");
			final Integer chargeDuration = rs.getInt("chargeDuration");
			final String durationType = rs.getString("durationType");
			final Integer taxInclusive = rs.getInt("taxInclusive");
			final String billFrequencyCode = rs.getString("billFrequencyCode");

			return new ChargeCodeData(id, chargeCode, chargeDescription,
					chargeType, chargeDuration, durationType, taxInclusive,
					billFrequencyCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #getChargeType() like RC ,NRC
	 */
	public List<ChargeTypeData> getChargeType() {

		final ChargeTypeDataMapper typeMapper = new ChargeTypeDataMapper();

		final String sql = "select mcv.id as id,mcv.code_value as chargeType from m_code_value mcv,m_code mc "
				+ "where mcv.code_id=mc.id and mc.code_name='Charge Type' order by mcv.id";

		return jdbcTemplate.query(sql, typeMapper);
	}

	private static final class ChargeTypeDataMapper implements
			RowMapper<ChargeTypeData> {

		public ChargeTypeData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String chargeType = rs.getString("chargeType");

			return new ChargeTypeData(id, chargeType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #getDurationType() like month(s),week(s),day(s)
	 */
	public List<DurationTypeData> getDurationType() {

		final DurationTypeDataMapper durationMapper = new DurationTypeDataMapper();

		final String sql = "select mcv.id as id,mcv.code_value as durationType from m_code_value mcv,m_code mc "
				+ "where mcv.code_id=mc.id and mc.code_name='Duration Type' order by mcv.id";

		return jdbcTemplate.query(sql, durationMapper);
	}

	private static final class DurationTypeDataMapper implements
			RowMapper<DurationTypeData> {

		public DurationTypeData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String durationTypeCode = rs.getString("durationType");
			return new DurationTypeData(id, durationTypeCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #getBillFrequency() like monthly,weekly,quaterly etc
	 */
	public List<BillFrequencyCodeData> getBillFrequency() {

		final BillFrequencyMapper frequencyMapper = new BillFrequencyMapper();

		final String sql = "select mcv.id as id,mcv.code_value as billFrequency from m_code_value mcv,m_code mc "
				+ "where mcv.code_id=mc.id and mc.code_name='Bill Frequency' order by mcv.id";

		return jdbcTemplate.query(sql, frequencyMapper);

	}

	private static final class BillFrequencyMapper implements
			RowMapper<BillFrequencyCodeData> {

		public BillFrequencyCodeData mapRow(final ResultSet rs, int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String billFrequencyCode = rs.getString("billFrequency");

			return new BillFrequencyCodeData(id, billFrequencyCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #retrieveSingleChargeCodeDetails(java.lang.Long)
	 */
	public ChargeCodeData retrieveSingleChargeCodeDetails(
			final Long chargeCodeId) {

		try {

			final ChargeCodeMapper mapper = new ChargeCodeMapper();

			final String sql = "select " + mapper.schema() + " where id = ?";

			return jdbcTemplate.queryForObject(sql, mapper,
					new Object[] { chargeCodeId });
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	private static final class ChargeCodeRecurringMapper implements RowMapper<ChargeCodeData> {

		public String schema() {
			/*return " cc.id as id, cc.charge_code AS chargeCode, pp.price AS price, cc.duration_type AS durationType," +
					" cc.charge_duration AS chargeDuration, cc.billfrequency_code AS billFrequencyCode " +
					" FROM (b_plan_pricing pp JOIN b_plan_master pm ON pm.id = pp.plan_id) " +
					" JOIN b_charge_codes cc ON pp.charge_code = cc.charge_code WHERE pm.is_deleted = 'n' " +
					" AND pp.plan_id = ?  and cc.billfrequency_code = ? group by cc.id";
			"pp.id as id,
		return " pp.id as id, pp.charge_code as chargeCode,pp.price as price,cc.duration_type as durationType,cc.charge_duration as chargeDuration, cc.billfrequency_code as billFrequencyCode, "+
					" ct.contract_type as contractType,ct.contract_duration as units from b_plan_pricing pp "+
					" JOIN b_plan_master pm ON (pm.id = pp.plan_id) JOIN b_charge_codes cc ON (pp.charge_code = cc.charge_code) "+
					" JOIN b_priceregion_master bprm ON (pp.price_region_id = bprm.id) JOIN b_priceregion_detail bpd ON (bprm.id=bpd.priceregion_id) "+
					" JOIN b_state bs ON ((bpd.state_id = bs.id or bpd.state_id=0)  and bpd.country_id=bs.parent_code AND bpd.is_deleted = 'N') "+
					" LEFT JOIN b_client_address bca ON (bca.state=bs.state_name and address_key='PRIMARY') "+
					" LEFT JOIN b_contract_period ct ON pp.duration=ct.contract_period "+
					" where pm.is_deleted = 'n' and pp.plan_id = ?  and cc.billfrequency_code=? and ct.contract_period=? and  bca.client_id=?  group by pp.id ";*/	
			return " pp.id AS id,pp.charge_code AS chargeCode,pp.price AS price,cc.duration_type AS durationType,cc.charge_duration AS chargeDuration," +
				   " cc.billfrequency_code AS billFrequencyCode,ct.contract_type AS contractType,ct.contract_duration AS units" +
				   " FROM b_plan_pricing pp JOIN b_plan_master pm ON (pm.id = pp.plan_id) JOIN b_charge_codes cc ON (pp.charge_code = cc.charge_code) " +
				   " JOIN b_priceregion_master bprm ON (pp.price_region_id = bprm.id) JOIN b_priceregion_detail pd ON (bprm.id = pd.priceregion_id) " +
				   " LEFT JOIN b_contract_period ct ON pp.duration = ct.contract_period WHERE pm.is_deleted = 'n' AND pp.plan_id = ?" +
				   " AND cc.billfrequency_code = ? AND ct.contract_period = ?" +
				   " AND (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_state c, b_charge_codes cc,b_client_address d" +
				   " WHERE  b.priceregion_id = a.price_region_id AND b.state_id = c.id AND a.price_region_id = b.priceregion_id AND d.state = c.state_name" +
				   " AND cc.charge_code = a.charge_code AND cc.billfrequency_code = ? AND d.address_key = 'PRIMARY' AND d.client_id = ? AND a.plan_id = ?),0)" +
				   " AND pd.country_id =ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_country c, b_charge_codes cc,b_client_address d" +
				   " WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code " +
				   " AND cc.billfrequency_code = ? AND a.price_region_id = b.priceregion_id AND c.country_name = d.country AND d.address_key = 'PRIMARY'" +
				   "AND d.client_id = ? AND a.plan_id = ?),0)) GROUP BY pp.id";
			
		}

		@Override
		public ChargeCodeData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String chargeCode = rs.getString("chargeCode");
			final BigDecimal price = rs.getBigDecimal("price");
			final String durationType = rs.getString("durationType");
			final Integer chargeDuration = rs.getInt("chargeDuration");
			final String billFrequencyCode = rs.getString("billFrequencyCode");
			final String contractType = rs.getString("contractType");
			final Integer units = rs.getInt("units");

			ChargeCodeData chargeCodeData = new ChargeCodeData(id,chargeCode, null, null, chargeDuration, durationType, null, billFrequencyCode,contractType,units);
			chargeCodeData.setPrice(price);
			
			return chargeCodeData;	
		}
	}
	
	@Override
	public ChargeCodeData retrieveChargeCodeForRecurring(Long planId,
			String billingFrequency,String contractPeriod,Long clientId) {
		try {

			final ChargeCodeRecurringMapper mapper = new ChargeCodeRecurringMapper();

			final String sql = "select " + mapper.schema();

			return jdbcTemplate.queryForObject(sql, mapper, new Object[] {planId, billingFrequency ,contractPeriod,billingFrequency,
					clientId,planId,billingFrequency,clientId,planId});
		
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

}
