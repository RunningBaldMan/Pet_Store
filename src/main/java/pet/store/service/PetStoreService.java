package pet.store.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;

	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		PetStore petStore = findOrCreatePetStore(petStoreData.getPetStoreId());
		copyPetStoreFields(petStore, petStoreData);
		PetStore savedPetStore = petStoreDao.save(petStore);
		return new PetStoreData(savedPetStore);
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		if (petStoreId == null) {
			return new PetStore();
		} else {
			return findPetStoreById(petStoreId);
		}
	}

	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store with ID=" + petStoreId + " not found"));
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
		petStore.setPetStoreId(petStoreData.getPetStoreId());
	}

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CustomerDao customerDao;

	@Transactional
	public Employee findEmployeeById(Long petStoreId, Long employeeId) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException("Employee not found with ID: " + employeeId));
		if (!employee.getPetStore().getEmployees().equals(petStoreId)) {
			throw new IllegalArgumentException("Employee does not belong to pet store with ID: " + petStoreId);
		}
		return employee;
	}

	@Transactional
	public Employee findOrCreateEmployee(Long employeeId, Long petStoreId) {
		if (employeeId == null) {
			return new Employee();
		} else {
			return findEmployeeById(petStoreId, employeeId);
		}
	}

	@Transactional
	public void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
	}

	@Transactional
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store not found with ID: " + petStoreId));

		Employee employee = findOrCreateEmployee(petStoreEmployee.getEmployeeID(), petStoreId);

		copyEmployeeFields(employee, petStoreEmployee);

		employee.setPetStore(petStore);

		petStore.getEmployees().add(employee);

		employee = employeeDao.save(employee);

		return convertToPetStoreEmployee(employee);
	}

	private PetStoreEmployee convertToPetStoreEmployee(Employee employee) {
		PetStoreEmployee petStoreEmployee = new PetStoreEmployee();
		petStoreEmployee.setEmployeeID(employee.getEmployeeID());
		petStoreEmployee.setEmployeeFirstName(employee.getEmployeeFirstName());
		return petStoreEmployee;
	}

	@Transactional
	public Customer findCustomerById(Long petStoreId, Long customerId) {
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));
		boolean found = customer.getPetStores().stream()
				.anyMatch(petStore -> petStore.getPetStoreId().equals(petStoreId));
		if (!found) {
			throw new IllegalArgumentException("Customer does not belong to pet store with ID: " + petStoreId);
		}
		return customer;
	}

	@Transactional
	public Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
		if (customerId == null) {
			return new Customer();
		} else {
			return findCustomerById(petStoreId, customerId);
		}
	}

	@Transactional
	public void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
	}

	@Transactional
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store not found with ID: " + petStoreId));

		Customer customer = findOrCreateCustomer(petStoreCustomer.getCustomerId(), petStoreId);

		copyCustomerFields(customer, petStoreCustomer);

		customer.getPetStores().add(petStore);

		customer = customerDao.save(customer);

		return convertToPetStoreCustomer(customer);
	}

	private PetStoreCustomer convertToPetStoreCustomer(Customer customer) {
		PetStoreCustomer petStoreCustomer = new PetStoreCustomer();
		petStoreCustomer.setCustomerId(customer.getCustomerId());
		petStoreCustomer.setCustomerFirstName(customer.getCustomerFirstName());
		return petStoreCustomer;
	}

	@Transactional
	public PetStoreData retrievePetStoreById(Long petStoreId) {
		PetStore petStore = petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store not found with ID: " + petStoreId));
		return convertToPetStoreData(petStore);
	}

	@Transactional
	public List<PetStoreData> retrieveAllPetStores() {
		return petStoreDao.findAll().stream().map(this::convertToPetStoreData).collect(Collectors.toList());
	}

	private PetStoreData convertToPetStoreData(PetStore petStore) {
		PetStoreData petStoreData = new PetStoreData();
		petStoreData.setPetStoreId(petStore.getPetStoreId());
		petStoreData.setPetStoreName(petStore.getPetStoreName());
		return petStoreData;
	}

	@Transactional
	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store not found with ID: " + petStoreId));
		petStoreDao.delete(petStore);
	}
}