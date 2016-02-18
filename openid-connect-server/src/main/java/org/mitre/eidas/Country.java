package org.mitre.eidas;

public class Country {
	
	private int id;
	private String name;
	private String url;
	private String countrySelector;
	
	Country(int id, String name, String url, String countrySelector){
		this.id=id;
		this.name=name;
		this.url=url;
		this.countrySelector=countrySelector;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCountrySelector() {
		return countrySelector;
	}

	public void setCountrySelector(String countrySelector) {
		this.countrySelector = countrySelector;
	}

    public String toString() {
        String spacer = " \n";
        return "Country id : " + this.id + spacer + "\tCountry name : " + this.name + spacer + "\tCountry url : " + this.url + spacer + "\tCountry selector : " + this.countrySelector;
    }
}
