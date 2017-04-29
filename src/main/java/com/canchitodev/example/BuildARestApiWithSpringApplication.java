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
package com.canchitodev.example;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.canchitodev.example.domain.Contact;
import com.canchitodev.example.service.ContactService;

@SpringBootApplication
public class BuildARestApiWithSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildARestApiWithSpringApplication.class, args);
	}
	
	@Bean
	InitializingBean contactInitializer(final ContactService contactService) {

	    return new InitializingBean() {
	        public void afterPropertiesSet() throws Exception {
	        	Contact contact = new Contact();
	        	contact.setLastName("Doe");
	        	contact.setMail("john@canchito-dev.com");
	        	contact.setFirstName("John");
	        	contact.setTelephone("123456");
	        	contactService.save(contact);
	        	
	        	contact = new Contact();
	        	contact.setLastName("Perez");
	        	contact.setMail("juan@canchito-dev.com");
	        	contact.setFirstName("Juan");
	        	contact.setTelephone("098765");
	        	contactService.save(contact);
	        }
	    };
	}
}
