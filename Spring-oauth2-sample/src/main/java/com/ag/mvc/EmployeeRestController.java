package com.ag.mvc;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ag.dto.Employee;

/**
 * The Class EmployeeRestController.
 */
@Controller
public class EmployeeRestController {

	/**
	 * Gets emplyoes.
	 *
	 * @param principal principal
	 * @return emplyoes
	 */
	@RequestMapping(value = "/rest/employees", produces = "application/json")
	public ResponseEntity<List<Employee>> getEmplyoes(Principal principal) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<List<Employee>>(getAllEmployess(), headers, HttpStatus.OK);
	}

	/**
	 * Gets all employess.
	 *
	 * @return all employess
	 */
	private List<Employee> getAllEmployess() {
		List<Employee> list = new ArrayList<Employee>();
		Employee emp1 = new Employee();
		emp1.setId(1);
		emp1.setName("emp1");
		emp1.setEmail("emp1@ag.com");
		list.add(emp1);
		Employee emp2 = new Employee();
		emp2.setId(2);
		emp2.setName("emp2");
		emp2.setEmail("emp2@ag.com");
		list.add(emp2);
		return list;
	}

}
