/**
 * This content is released under the MIT License (MIT)
 *
 * Copyright (c) 2017, canchito-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * @author 		José Carlos Mendoza Prego
 * @copyright	Copyright (c) 2017, canchito-dev (http://www.canchito-dev.com)
 * @license		http://opensource.org/licenses/MIT	MIT License
 * @link		https://github.com/canchito-dev/build-a-rest-api-with-spring
 **/
package com.canchitodev.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canchitodev.example.domain.Contact;
import com.canchitodev.example.exception.ObjectNotFoundException;
import com.canchitodev.example.repository.ContactRepository;

@Service
@Transactional
public class ContactService {
	
	@Autowired
	private ContactRepository contactRepository;
	
	public Contact findById(Long uuid) {
		Contact contact = this.contactRepository.findOne(uuid);
		if(contact == null)
			throw new ObjectNotFoundException("Could not find contact with id '" + uuid + "'");
		return contact;
	}
	
	public List<Contact> findAll() {
		return this.contactRepository.findAll();
	}
	
	public void save(Contact contact) {
		this.contactRepository.save(contact);
	}

	public void update(Contact contact) {
		Contact entity = this.findById(contact.getUuid());
		
		if(contact.getLastName() == null)
			contact.setLastName(entity.getLastName());
		if(contact.getMail() == null)
			contact.setMail(entity.getMail());
		if(contact.getFirstName() == null)
			contact.setFirstName(entity.getFirstName());
		if(contact.getTelephone() == null)
			contact.setTelephone(entity.getTelephone());
		
		this.save(contact);
	}
	
	public void delete(Long uuid) {
		Contact contact = this.findById(uuid);
		this.contactRepository.delete(contact);
	}
}