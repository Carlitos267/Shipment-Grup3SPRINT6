package cat.institutmarianao.shipments.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import cat.institutmarianao.shipments.model.Assignment;
import cat.institutmarianao.shipments.model.Delivery;
import cat.institutmarianao.shipments.model.Shipment;
import cat.institutmarianao.shipments.model.Shipment.Status;
import cat.institutmarianao.shipments.model.User;
import cat.institutmarianao.shipments.model.forms.ShipmentsFilter;
import cat.institutmarianao.shipments.services.ShipmentService;
import cat.institutmarianao.shipments.services.UserService;

@Controller
@SessionAttributes({ "user" })
@RequestMapping(value = "/shipments")
public class ShipmentController {

	@Autowired
	private UserService userService;

	@Autowired
	private ShipmentService shipmentService;

	@ModelAttribute("user")
	public User setupUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return (User) userService.loadUserByUsername(username);
	}

	@GetMapping("/new")
	public ModelAndView newShipment() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		ModelAndView modelAndView = new ModelAndView("shipment");
		Shipment shipment = new Shipment();
		shipment.setReceptionist(username);
		modelAndView.getModelMap().addAttribute("shipment", shipment);
		return modelAndView;
	}

	@PostMapping("/new")
	public String submitNewShipment(@Validated Shipment shipment, BindingResult result) {
		if (result.hasErrors()) {
			return "shipment";
		}

		shipmentService.add(shipment);
		return "redirect:/shipments/list/PENDING";
	}

	@GetMapping("/list/{shipment-status}")
	public ModelAndView allShipmentsList(@ModelAttribute("user") User user,
			@PathVariable("shipment-status") Status shipmentStatus) {

		ModelAndView shipmentsView = new ModelAndView("shipments");
		Delivery delivery = new Delivery();
		delivery.setPerformer(user.getUsername());
		Assignment assignment = new Assignment();
		assignment.setPerformer(user.getUsername());
		assignment.setPriority(2);
		ShipmentsFilter shipmentFilter = new ShipmentsFilter();
		shipmentFilter.setStatus(shipmentStatus);
		shipmentsView.getModelMap().addAttribute("userRole", user.getRole());
		shipmentsView.getModelMap().addAttribute("shipmentStatus", shipmentStatus);

		switch (user.getRole()) {
		case COURIER:
			shipmentFilter.setCourierAssigned(user.getUsername());
			shipmentsView.getModelMap().addAttribute("shipments", shipmentService.filterShipments(shipmentFilter));
			shipmentsView.getModelMap().addAttribute("assignment", assignment);
			shipmentsView.getModelMap().addAttribute("delivery", delivery);
			break;
		case RECEPTIONIST:
			shipmentFilter.setReceptionist(user.getUsername());
			shipmentsView.getModelMap().addAttribute("shipments", shipmentService.filterShipments(shipmentFilter));
			shipmentsView.getModelMap().addAttribute("assignment", assignment);
			shipmentsView.getModelMap().addAttribute("delivery", delivery);
			shipmentsView.getModelMap().addAttribute("couriers", userService.getAllCourier());
			break;
		case LOGISTICS_MANAGER:
			shipmentsView.getModelMap().addAttribute("shipments", shipmentService.filterShipments(shipmentFilter));
			shipmentsView.getModelMap().addAttribute("assignment", assignment);
			shipmentsView.getModelMap().addAttribute("delivery", delivery);
			shipmentsView.getModelMap().addAttribute("couriers", userService.getAllCourier());
			break;
		}

		return shipmentsView;
	}

	@PostMapping("/assign")
	public String assignShipment(@Validated Assignment assignment, BindingResult result) {
		assignment.setDate(new Date());
		shipmentService.tracking(assignment);
		return "redirect:/shipments/list/PENDING";
	}

	@PostMapping("/deliver")
	public String deliverShipment(@Validated Delivery delivery, BindingResult result) {
		delivery.setDate(new Date());
		shipmentService.tracking(delivery);
		return "redirect:/shipments/list/IN_PROCESS";
	}
}
