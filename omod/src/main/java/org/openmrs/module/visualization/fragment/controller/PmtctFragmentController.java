/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.visualization.fragment.controller;

import org.openmrs.api.UserService;
import org.openmrs.module.visualization.Model.BarChartModel;
import org.openmrs.module.visualization.Model.ChartModel;
import org.openmrs.module.visualization.Model.PmtctCohortRetentiModel;
import org.openmrs.module.visualization.Model.ViralSuppressionModel;
import org.openmrs.module.visualization.Utility.DbPatientUtils;
import org.openmrs.module.visualization.Utility.DbPmtctUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 *  * Controller for a fragment that shows all users  
 */
public class PmtctFragmentController {
	
	public void controller(FragmentModel model, @SpringBean("userService") UserService service) {
		model.addAttribute("users", service.getAllUsers());
	}
	
	public @ResponseBody
	ArrayList<ChartModel> getAncPmtctArt() {
		DbPmtctUtils utils = new DbPmtctUtils();
		return utils.getAncPmtctArt();
	}
	
	public @ResponseBody
	ArrayList<PmtctCohortRetentiModel> getPmtctCohortRetention(@RequestParam(value = "year") int year,
	        @RequestParam(value = "month") int month) {
		if (year < 1) {
			year = LocalDate.now().getYear();
			month = LocalDate.now().getMonthValue();
		}
		LocalDate currentSel = LocalDate.of(year, month, 1);
		ArrayList<PmtctCohortRetentiModel> pmtctCohortRetention = new DbPmtctUtils().getPmtctCohortRetention(currentSel);
		return pmtctCohortRetention;
	}
	
	public @ResponseBody
	ArrayList<ViralSuppressionModel> getPmtctCohortViralSuppression(@RequestParam(value = "year") int year,
	        @RequestParam(value = "month") int month) {
		if (year < 1) {
			year = LocalDate.now().getYear();
			month = LocalDate.now().getMonthValue();
		}
		LocalDate currentSel = LocalDate.of(year, month, 1);
		return new DbPmtctUtils().getPmtctCohortViralSuppression(currentSel);
	}
}
