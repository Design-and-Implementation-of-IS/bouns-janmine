package model;


public class Wine {
    private String wineId;
    private String name;
    private int year;
    private double price;
    private String sweetnessLevel;
	private String productImage;
	private String description;
	private String catalogNumber;

    
	public Wine() {
		super();
	}
	
	
	public Wine(String wineId, String name, int year, double price, String sweetnessLevel) {
		super();
		this.wineId = wineId;
		this.name = name;
		this.year = year;
		this.price = price;
		this.sweetnessLevel = sweetnessLevel;
	}


	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCatalogNumber() {
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

	public String getWineId() {
		return wineId;
	}
	public void setWineId(String wineId) {
		this.wineId = wineId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getSweetnessLevel() {
		return sweetnessLevel;
	}
	public void setSweetnessLevel(String sweetnessLevel) {
		this.sweetnessLevel = sweetnessLevel;
	}


	@Override
	public String toString() {
		return "Wine [wineId=" + wineId + ", name=" + name + ", year=" + year + ", price=" + price + ", sweetnessLevel="
				+ sweetnessLevel + "]";
	}
	
	
    
}