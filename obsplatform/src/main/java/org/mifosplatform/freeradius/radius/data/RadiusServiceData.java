package org.mifosplatform.freeradius.radius.data;

import java.util.List;

import org.json.JSONObject;

public class RadiusServiceData {

	private Long id;
	private String serviceName;
	private String upRate;
	private String downRate;
	private Long nextServicId;
	private Long trafficUnitdl;
	private String nextService;
	private boolean limitComb;
	private boolean limitExpiration;
	private String radVersion;
	private List<RadiusServiceData> radServiceTemplateData;

	public RadiusServiceData(Long id, String serviceName, String downRate,
			String upRate, Long nextServicId, Long trafficUnitdl,
			String nextService) {

		this.id = id;
		this.serviceName = serviceName;
		this.downRate = downRate;
		this.upRate = upRate;
		this.nextServicId = nextServicId;
		this.trafficUnitdl = trafficUnitdl;
		this.nextService = nextService;
	}

	public RadiusServiceData(final Long id, final String serviceName,
			final String downRate, final String upRate,
			final Long trafficUnitdl, final Long nextServiceId,
			final Long limitComb, final Long limitExpiration) {

		this.id = id;
		this.serviceName = serviceName;
		this.downRate = downRate;
		this.upRate = upRate;
		this.nextServicId = nextServiceId;
		this.trafficUnitdl = trafficUnitdl;
		this.limitComb = (limitComb == 0 ? false : true);
		this.limitExpiration = (limitExpiration == 0 ? false : true);

	}

	public RadiusServiceData(final String radVersion, JSONObject jsonObject) {
		this.radVersion = radVersion;

	}

	public Long getId() {
		return id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getUpRate() {
		return upRate;
	}

	public String getDownRate() {
		return downRate;
	}

	public Long getNextServicId() {
		return nextServicId;
	}

	public Long getTrafficUnitdl() {
		return trafficUnitdl;
	}

	public String getNextService() {
		return nextService;
	}

	public boolean isLimitComb() {
		return limitComb;
	}

	public void setLimitComb(boolean limitComb) {
		this.limitComb = limitComb;
	}

	public boolean isLimitExpiration() {
		return limitExpiration;
	}

	public void setLimitExpiration(boolean limitExpiration) {
		this.limitExpiration = limitExpiration;
	}

	public String getRadVersion() {
		return radVersion;
	}

	public void setRadVersion(String radVersion) {
		this.radVersion = radVersion;
	}

	public List<RadiusServiceData> getRadServiceTemplateData() {
		return radServiceTemplateData;
	}

	public void setRadServiceTemplateData(List<RadiusServiceData> radServiceTemplateData) {
		this.radServiceTemplateData = radServiceTemplateData;
	}

}
