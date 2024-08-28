package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

class DailyStatus {
	private int failed;
	private int total;
	private int noOfDelivery;

	public DailyStatus(int failed, int total, int noOfDelivery) {
		// TODO Auto-generated constructor stub
		this.failed = failed;
		this.total = total;
		this.noOfDelivery = noOfDelivery;
	}

	String getHtmlStringForDay() {
		String html = new String("");
		if (failed == 0) {
			html = "" + "<td id=pass>" + failed + "</td><td id=pass>" + total + "</td><td id=pass>" + noOfDelivery
					+ "</td>";
		} else {
			html = "" + "<td id=fail>" + failed + "</td><td id=fail>" + total + "</td><td id=fail>" + noOfDelivery
					+ "</td>";
		}
		return html;

	}

	String getPlainStringForDay() {
		String plain = new String("");
		plain += "failed::" + failed + ",total::" + total + ",noOfDelivery::" + noOfDelivery;
		return plain;

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getHtmlStringForDay();
		
	//	 return getPlainStringForDay();
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getNoOfDelivery() {
		return noOfDelivery;
	}

	public void setNoOfDelivery(int noOfDelivery) {
		this.noOfDelivery = noOfDelivery;
	}

	public void addFailed(int failed) {
		this.failed += failed;
	}

	public void addTotal(int total) {
		this.total += total;
	}

	public void addNoOfDelivery(int noOfDelivery) {
		this.noOfDelivery += noOfDelivery;
	}

}
