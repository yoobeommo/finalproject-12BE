package com.example.finalproject12be.domain.store.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.example.finalproject12be.domain.store.dto.StoreRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Store_3 {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "STORE_ID")
	private Long id;

	@Column
	private String gu;

	@Column
	private String detailAddress;

	@Column
	private String name;

	@Column
	private String callNumber;

	@Column
	private String businessHours;

	@Column
	private Double longitude;

	@Column
	private Double latitude;

	@Column
	private Integer nightPharmacy;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Store_3_language> languages = new ArrayList<>();

	public void addLanguage(Store_3_language language) {
		languages.add(language);
		language.setStore(this);
	}

	public void removeLanguage(Store_3_language language) {
		languages.remove(language);
		language.setStore(null);
	}


	public Store_3(String address, String name, String callNumber, String weekdaysTime, String saturdayTime, String sundayTime, String holidayTime, Double longitude, Double latitude){
		//this.address = address;
		this.name = name;
		this.callNumber = callNumber;
		//this.weekdaysTime = storeRequest.getWeekdaysTime();
		//this.saturdayTime = storeRequest.getSaturdayTime();
		//this.sundayTime = storeRequest.getSundayTime();
		//this.holidayTime = storeRequest.getHolidayTime();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Store_3(StoreRequest storeRequest) {
		//this.address = storeRequest.getAddress();
		this.name = storeRequest.getName();
		this.callNumber = storeRequest.getCallNumber();
		//this.weekdaysTime = storeRequest.getWeekdaysTime();
		//this.saturdayTime = storeRequest.getSaturdayTime();
		//this.sundayTime = storeRequest.getSundayTime();
		//this.holidayTime = storeRequest.getHolidayTime();
		this.longitude = storeRequest.getLongitude();
		this.latitude = storeRequest.getLatitude();
		// this.foreignLanguage = storeRequest.getForeignLanguage();
		// this.english = storeRequest.getEnglish();
		// this.chinese =storeRequest.getChinese();
		// this.japanese = storeRequest.getJapanese();
		this.nightPharmacy = storeRequest.getNightPharmacy();
	}

	// public void setForeign(int foreignLanguage, int english, int chinese, int japanese){
	// 	this.foreignLanguage = foreignLanguage;
	// 	this.english = english;
	// 	this.japanese = japanese;
	// 	this.chinese = chinese;
	// }

	public void updateStore(StoreRequest storeRequest) {
		//his.address = storeRequest.getAddress();
		this.name = storeRequest.getName();
		this.callNumber = storeRequest.getCallNumber();
		//this.weekdaysTime = storeRequest.getWeekdaysTime();
		//this.saturdayTime = storeRequest.getSaturdayTime();
		//this.sundayTime = storeRequest.getSundayTime();
		//this.holidayTime = storeRequest.getHolidayTime();
		this.longitude = storeRequest.getLongitude();
		this.latitude = storeRequest.getLatitude();
		// this.foreignLanguage = storeRequest.getForeignLanguage();
		// this.english = storeRequest.getEnglish();
		// this.chinese =storeRequest.getChinese();
		// this.japanese = storeRequest.getJapanese();
		this.nightPharmacy = storeRequest.getNightPharmacy();
	}

}
