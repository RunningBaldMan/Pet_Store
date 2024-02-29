package pet.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pet.store.dao.PetStoreDao;

public class PetStoreService {
	
	@Service
	private class PetStpreService {
		
		private final PetStoreDao petStoreDao;
		
		@Autowired
		public PetStpreService(PetStoreDao petStoreDao) {
			this.petStoreDao = petStoreDao;
		}
	}

}
