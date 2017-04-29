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
package com.canchitodev.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.canchitodev.example.domain.Contact;
import com.canchitodev.example.service.ContactService;

@RestController
@RequestMapping("/contact")
public class ContactController {
	
	@Autowired
	private ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Contact>> findAll() {
		List<Contact> contacts = this.contactService.findAll();
		return new ResponseEntity<List<Contact>>(contacts, HttpStatus.OK);
	}
	
	@RequestMapping(value="/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Contact> findOne(@PathVariable Long contactId) {
		Contact contact = this.contactService.findById(contactId);
		return new ResponseEntity<Contact>(contact, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Contact> create(@RequestBody Contact contact) {
		this.contactService.save(contact);
		return new ResponseEntity<Contact>(contact, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/{contactId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Contact> update(@PathVariable Long contactId, @RequestBody Contact contact) {
		contact.setUuid(contactId);
		this.contactService.update(contact);
		return new ResponseEntity<Contact>(contact, HttpStatus.OK);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/{contactId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody HttpEntity delete(@PathVariable Long contactId) {
		this.contactService.delete(contactId);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}