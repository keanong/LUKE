package com.xdel.luke.Locker;

public class LockerStation {
    public String locker_station_id;
    public String locker_operator_id;
    public String locker_station_name;
    public String locker_station_description;
    public String opening_hours;
    public String block_number;
    public String building_name;
    public String street_name;
    public String state;
    public String country_code;
    public String postal_code;
    public double latitude;
    public double longitude;
    public double locker_station_distance;
    public String locker_usage_popularity;
    public boolean is_return_eligible;
    public Delivery_Feature[] delivery_feature_available;
    public Payment_Option[] payment_options_supported;
    public String[] valid_postal_codes;
    public String lsp_user_close;
    public String lsp_user_open;
    public boolean is_otp_returns;
    public boolean is_otp_collection;
    public boolean is_otp_lsp_user;
    public String created_date_time;
    public String updated_date_time;
    public boolean locker_disabled;

    public LockerStation(String alocker_station_id, String alocker_operator_id, String alocker_station_name, String alocker_station_description, String aopening_hours,
                         String ablock_number, String abuilding_name, String astreet_name, String astate, String acountry_code, String apostal_code,
                         double alatitude, double alongitude, double alocker_station_distance, String alocker_usage_popularity, boolean ais_return_eligible,
                         String alsp_user_close, String alsp_user_open, boolean ais_otp_returns, boolean ais_otp_collection, boolean ais_otp_lsp_user,
                         String acreated_date_time, String aupdated_date_time, boolean alocker_disabled,
                         Delivery_Feature[] adelivery_feature_available, Payment_Option[] apayment_options_supported, String[] avalid_postal_codes){
        this.locker_station_id = alocker_station_id;
        this.locker_operator_id = alocker_operator_id;
        this.locker_station_name = alocker_station_name;
        this.locker_station_description = alocker_station_description;
        this.opening_hours = aopening_hours;
        this.block_number = ablock_number;
        this.building_name = abuilding_name;
        this.street_name = astreet_name;
        this.state = astate;
        this.country_code = acountry_code;
        this.postal_code = apostal_code;
        this.latitude = alatitude;
        this.longitude = alongitude;
        this.locker_station_distance = alocker_station_distance;
        this.locker_usage_popularity = alocker_usage_popularity;
        this.is_return_eligible = ais_return_eligible;
        this.lsp_user_close = alsp_user_close;
        this.lsp_user_open = alsp_user_open;
        this.is_otp_returns = ais_otp_returns;
        this.is_otp_collection = ais_otp_collection;
        this.is_otp_lsp_user = ais_otp_lsp_user;
        this.created_date_time = acreated_date_time;
        this.updated_date_time = aupdated_date_time;
        this.locker_disabled = alocker_disabled;
        this.delivery_feature_available = adelivery_feature_available;
        this.payment_options_supported = apayment_options_supported;
        this.valid_postal_codes = avalid_postal_codes;
    }

}
