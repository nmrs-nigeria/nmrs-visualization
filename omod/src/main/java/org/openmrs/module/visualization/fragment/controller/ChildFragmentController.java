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
import org.openmrs.module.visualization.Utility.DbPatientUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *  * Controller for a fragment that shows all users  
 */
public class ChildFragmentController {
	
	public void controller(FragmentModel model, @SpringBean("userService") UserService service) {
		model.addAttribute("users", service.getAllUsers());
	}
	
	public String getLineList(HttpServletRequest request) {
		return "success";
	}
	
	/*public @ResponseBody
	ArrayList<ChartModel> getPmtctFo() {
		DbPatientUtils utils = new DbPatientUtils();
		return utils.getPmtctFollowUp();
	}*/
}
