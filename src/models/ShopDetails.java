package models;

public class ShopDetails {
    private int shopID;
    private String shopName;
    private String addressLine1;
    private String addressLine2;
    private String contactNumber;
    private String email;
    private String website;
    private byte[] logo;

    // Getters and Setters
    public int getShopID() {
        return shopID;
    }

    public void setShopID(int shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    // Convenience method to get full address
    public String getFullAddress() {
        String full = addressLine1 != null ? addressLine1 : "";
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            full += (full.isEmpty() ? "" : ", ") + addressLine2;
        }
        return full;
    }

    // Backward compatibility method
    public String getAddress() {
        return getFullAddress();
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}