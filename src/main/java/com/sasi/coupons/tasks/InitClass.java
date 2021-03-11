package com.sasi.coupons.tasks;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sasi.coupons.exceptions.ApplicationException;
import com.sasi.coupons.logic.CouponsController;

@Component
public class InitClass {

	@Autowired
	private CouponsController couponsController;

	@PostConstruct
	@Scheduled(cron = "0 0 0 ? * * *") // Scheduled to every day at midnight - 00:00:00am.
	public void init() throws ApplicationException {

		// Replace the TimerTask for deleting the expired coupons every day at midnight.
		// Using Spring annotations to manage it instead of creating TimerTask class and initialize it in another class.
		this.couponsController.deleteAllExpiredCoupons();
	}
}
