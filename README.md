# build-a-rest-api-with-spring
In this guide, you will learn how to set up and build a simple REST API with Spring, that provides CRUD operations for entries that are saved into a database. In addition, you will learn how to map HTTP request to specific URL and its response codes, and how to handle unmapped requests.

<h2 class="sect1" style="text-align: justify;">What you’ll need</h2>
<ul style="text-align: justify;">
 	<li>About 30 minutes</li>
 	<li>A favorite IDE or Spring Tool Suite™ already install</li>
 	<li><a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html" target="_blank" rel="noopener noreferrer">JDK 6</a> or later</li>
</ul>
<h2 style="text-align: justify;">Introduction</h2>
<p style="text-align: justify;">As an example, we will be creating a service that accepts HTTP GET, POST, PUT and DELETE requests for performing basic CRUD operations on contacts. The requirements of our REST API are:</p>

<ul style="text-align: justify;">
 	<li>A <strong><em>POST</em> </strong>request send to <code>http://localhost:8080/contact/</code> must create a new contact entry in the database by using the information found from the request body and return a 200 response code on success as well as the information of the created contact entry including the unique <em>id</em> in the response body.</li>
 	<li>A <strong><em>DELETE</em> </strong>request send to <code>http://localhost:8080/contact/{id}</code> must delete the contact entry referenced by the <em>id</em> is found from the URL and return a 200 response code on success or 404 if the requested contact id was not found.</li>
 	<li>A <strong><em>GET</em> </strong>request send to <code>http://localhost:8080/contact/</code> must return a 200 response code on success as well as all contact entries that are found in the database.</li>
 	<li>A <strong><em>GET</em> </strong>request send to <code>http://localhost:8080/contact/{id}</code> must return a 200 response code on success as well as the information of the contact entry whose <em>id</em> is found from the URL and return a 200 response code on success or 404 if the requested contact id was not found.</li>
 	<li>A <strong><em>PUT</em> </strong>request send to<code> http://localhost:8080/contact/{id}</code> must update the information of an existing contact entry by using the information found from the request body and return a 200 response code on success as well as the information of the updated contact entry or 404 if the requested contact <em>id</em> was not found.</li>
</ul>
<p style="text-align: justify;">In order to achieve this, we will follow these steps:</p>

<ol style="text-align: justify;">
 	<li>Add the needed dependencies.</li>
 	<li>Create the entity that contains the information of a single contact entry.</li>
 	<li>Create the repository interface with methods supporting reading, updating, deleting and creating contacts against a H2 database.</li>
 	<li>Create the service layer that is responsible of mapping contacts into domain objects and vice versa.</li>
 	<li>Create the controller class that processes HTTP requests and returns the correct response back to the client.</li>
</ol>
<p style="text-align: justify;">Spring Boot does not require any specific code layout to work, however, it is recommend that you locate your main application class in a root package above other classes. Here is the layout we will be using:</p>

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-linenumbers="false" data-enlighter-theme="enlighter">com
 +- canchitodev
     +- example
         +- DemoprojectApplication.java
         |
         +- domain
         |   +- Contact.java
         |
         +- exception
         |   +- ConflictException.java
         |   +- ContentNotSupportedException.java
         |   +- ErrorInformation.java
         |   +- ForbiddenException.java
         |   +- GlobalExceptionHandler.java
         |   +- ObjectNotFoundException.java
         |
         +- repository
         |   +- ContactRepository.java
         |
         +- service
         |   +- ContactService.java
         |
         +- controller
             +- ContactrController.java</pre>
<p style="text-align: justify;">So let's get started!</p>

<h2 style="text-align: justify;">Getting Started</h2>
<p style="text-align: justify;">This tutorial assumes you can create a project with the help of Spring Initializr or Spring Tool Suite™. If you have not, please follow the steps from this post <a href="http://canchito-dev/blog/2017/04/16/build-a-project-with-spring-initializer-or-spring-tool-suite/" target="_blank" rel="noopener noreferrer">Build a project with Spring Initializr or Spring Tool Suite™</a> and include the following dependencies: Web, JPA and H2.</p>
<p style="text-align: justify;">On the other hand, if you already have an empty project, you can just add the needed dependencies by modifying the <code>pom.xml</code> file as follow:</p>

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="classic">&lt;dependencies&gt;
  &lt;dependency&gt;
    &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
    &lt;artifactId&gt;spring-boot-starter-data-jpa&lt;/artifactId&gt;
  &lt;/dependency&gt;
  &lt;dependency&gt;
    &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
    &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
  &lt;/dependency&gt;
  &lt;dependency&gt;
    &lt;groupId&gt;com.h2database&lt;/groupId&gt;
    &lt;artifactId&gt;h2&lt;/artifactId&gt;
    &lt;scope&gt;runtime&lt;/scope&gt;
  &lt;/dependency&gt;
  &lt;dependency&gt;
    &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
    &lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
    &lt;scope&gt;test&lt;/scope&gt;
  &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>
<p style="text-align: justify;"> Allow me to give a brief description of what each dependency does:</p>

<ul style="text-align: justify;">
 	<li style="text-align: justify;"><strong>spring-boot-starter-data-jpa:</strong> Uses data access technologies with enhanced support for JPA based data access layers.</li>
 	<li style="text-align: justify;"><strong>spring-boot-starter-web:</strong> Starter for building web, including RESTful, applications using Spring MVC. Uses Tomcat as the default embedded container</li>
 	<li style="text-align: justify;"><strong>h2:</strong> H2 database engine. Creates an in memory database.</li>
 	<li style="text-align: justify;"><strong>spring-boot-starter-test:</strong> Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito.</li>
</ul>
<h2 style="text-align: justify;">Creating the Entity Class</h2>
<p style="text-align: justify;">An entity is a representation of a database register. In our case, it is a class representing a contact. Let's define the contact class under the domain package.</p>

<pre class="EnlighterJSRAW" data-enlighter-theme="classic">package com.canchitodev.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Email;

@Entity
public class Contact {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "uuid", unique = true, nullable = false, length = 255)
  private Long uuid;
  
  @Column(name = "first_name", nullable = false, length = 60)
  private String firstName;
  
  @Column(name = "last_name", nullable = false, length = 60)
  private String lastName;
  
  @Column(name = "telephone", nullable = false, length = 60)
  private String telephone;
  
  @Email
  @Column(name = "mail", nullable = false, length = 60)
  private String mail;
  
  public Contact() {}

  public Contact(Long uuid, String firstName, String lastName, String telephone, String mail) {
    this.uuid = uuid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.telephone = telephone;
    this.mail = mail;
  }
  
  //Getters and setters removed for simplicity

  @Override
  public String toString() {
    return "Contact [uuid=" + uuid + ", firstName=" + firstName + ", lastName=" + lastName + ", telephone=" + telephone
        + ", mail=" + mail + "]";
  }
}</pre>
<p style="text-align: justify;">Here you have a <code>Contact </code>class with five attributes, the <code>uuid</code>, the <code>firstName</code>, the <code>lastName, the </code><code>telephone</code>, and the <code>mail</code>. You also have two constructors. The default constructor only exists because it is needed by JPA.  The other constructor is the one you’ll use to create instances of <code>Contact</code> to be saved to the database.</p>
<p style="text-align: justify;">The <code>@Entity</code> annotation specifies that this class is a JPA entity. And since there is no <code>@Table</code> annotation, JPA assumes that it is mapped to a table called <code>Contact</code>.</p>
<p style="text-align: justify;">The property <code>uuid</code> is annotated with the <code>@Id</code> so that JPA will recognize it as the object’s ID. In addition, the annotation <code>@GeneratedValue</code> tell JPA that this property should be automatically generated following the strategy indicated by <code>GenerationType.AUTO</code>.</p>
<p style="text-align: justify;">The rest of the properties are annotated with <code>@Column</code>, which means that they are mapped to a column with the name specified by name. <code>@Column</code>'s other properties just indicate that the value cannot be null and its max length.</p>
<p style="text-align: justify;">Finally, you can also see the <code>@Email</code> annotation. This validates that the column must be a valid e-mail address.</p>

<h2 style="text-align: justify;">Creating the Repository Class</h2>
<p style="text-align: justify;">The repository is an interface which allows CRUD operations on an entity.</p>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="classic">package com.canchitodev.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.canchitodev.example.domain.Contact;

@Repository
public interface ContactRepository extends JpaRepository&lt;Contact, Long&gt;, JpaSpecificationExecutor&lt;Contact&gt; {

}</pre>
<p style="text-align: justify;">As you can see, we are extending the interface with <code>JpaRepository</code>. By doing this, we inherit several methods which will allow us to work with <code>Contact</code> persistence, including methods for saving, deleting, updating and finding entities. Moreover, we also extend it with <code>JpaSpecificationExecutor</code>. Thanks to this, we will also be able to searches based on query criteria. For now, just keep in mind, that we will be able to do some basic operations on entities, just by extending it with <code>JpaRepository</code>.</p>

<h2 style="text-align: justify;">Creating the Service Class</h2>
<p style="text-align: justify;">In the service class, you will write all your logic. Once you have added your required validations and data manipulations, you call the repository. Note that this class is not required, as this could be done in the controller class. However, in my opinion it it good practice to separate the logic from the controller. Mainly because you can use the service in other parts of your application.</p>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="classic">package com.canchitodev.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canchitodev.example.domain.Contact;
import com.canchitodev.example.repository.ContactRepository;

@Service
@Transactional
public class ContactService {
  
  @Autowired
  private ContactRepository contactRepository;
  
  public Contact findById(Long uuid) {
    Contact contact = this.contactRepository.findOne(uuid);
    return contact;
  }
  
  public List&lt;Contact&gt; findAll() {
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
}</pre>
<p style="text-align: justify;">The <code>@Service</code> annotation allows for implementation classes to be autodetected through classpath scanning. Meanwhile, the <code>@Transactional</code> annotation describes transaction attributes on a method or class.</p>
<p style="text-align: justify;">You might find it curious that in the <code>update()</code> method, we are sending a <code>Contact</code> as an argument. this is because as you will see when we implement the controller class, the request body is automatically mapped to a <code>Contact</code> object, but the fields that are not to be updated are null in this object. As a consequence, we need to get the already stored <code>Contact</code>, and merge the new information with the one already stored, before saving it.</p>

<h2 style="text-align: justify;">Creating the Controller Class</h2>
<p style="text-align: justify;">A controller is the entry point from where all the HTTP request are handled. These components are easily identified by the <code>@RestController</code> annotation.</p>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="classic">package com.canchitodev.example.controller;

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
  public ResponseEntity&lt;List&lt;Contact&gt;&gt; findAll() {
    List&lt;Contact&gt; contacts = this.contactService.findAll();
    return new ResponseEntity&lt;List&lt;Contact&gt;&gt;(contacts, HttpStatus.OK);
  }
  
  @RequestMapping(value="/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity&lt;Contact&gt; findOne(@PathVariable Long contactId) {
    Contact contact = this.contactService.findById(contactId);
    return new ResponseEntity&lt;Contact&gt;(contact, HttpStatus.OK);
  }
  
  @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity&lt;Contact&gt; create(@RequestBody Contact contact) {
    this.contactService.save(contact);
    return new ResponseEntity&lt;Contact&gt;(contact, HttpStatus.CREATED);
  }
  
  @RequestMapping(value="/{contactId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity&lt;Contact&gt; update(@PathVariable Long contactId, @RequestBody Contact contact) {
    contact.setUuid(contactId);
    this.contactService.update(contact);
    return new ResponseEntity&lt;Contact&gt;(contact, HttpStatus.OK);
  }
  
  @SuppressWarnings("rawtypes")
  @RequestMapping(value="/{contactId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody HttpEntity delete(@PathVariable Long contactId) {
    this.contactService.delete(contactId);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }
}</pre>
<p style="text-align: justify;">The <code>@RequestMapping</code> annotation specifies the URL which the method is mapped to. For instance, a request to <code>http://localhost:8080/contact/</code> with a request method <em><strong>GET</strong></em>, will return all the Contact objects stored in the database.</p>

<h2 style="text-align: justify;">Exception Handling</h2>
<p style="text-align: justify;">There are at least three methods for handling exceptions:</p>

<ol style="text-align: justify;">
 	<li style="text-align: justify;">Using HTTP status codes</li>
 	<li style="text-align: justify;">Using a controller based exception handler</li>
 	<li style="text-align: justify;">And finally, using a global exception handler</li>
</ol>
<p style="text-align: justify;">We are only going to focus on the third option, the global exception handler, as it applies to all the controllers. Any class annotated with <code>@ControllerAdvice</code> becomes a controller-advice.</p>
<p style="text-align: justify;">First we need to create a class called <code>ErrorInformation</code>. Its solo purpose is to return the information about the error that was caught.</p>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="classic">package com.canchitodev.example.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ErrorInformation {
    private String message;
    private String exception;

    public ErrorInformation(String message, Exception ex) {
        this.message = message;
        if (ex != null) {
          this.exception = ex.getLocalizedMessage();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setException(String exception) {
        this.exception = exception;
    }
    
    @JsonInclude(Include.NON_NULL)
    public String getException() {
        return exception;
    }
}</pre>
<p style="text-align: justify;">Here is our global exception handler controller class. Notice the class is annotated with <code>@ControllerAdvice</code> annotation. Also methods are annotated with <code>@ExceptionHandler</code> annotation. Note that we also created the exception classes that are detected by the exception handler.</p>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="classic">package com.canchitodev.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE) // 415
  @ExceptionHandler(ContentNotSupportedException.class)
  @ResponseBody
  public ErrorInformation handleNotSupported(ContentNotSupportedException e) {
    return new ErrorInformation("Content is not supported", e);
  }
  
  @ResponseStatus(HttpStatus.CONFLICT) // 409
  @ExceptionHandler(ConflictException.class)
  @ResponseBody
  public ErrorInformation handleConflict(ConflictException e) {
    return new ErrorInformation("Conflict", e);
  }
  
  @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
  @ExceptionHandler(ObjectNotFoundException.class)
  @ResponseBody
  public ErrorInformation handleNotFound(ObjectNotFoundException e) {
      return new ErrorInformation("Not found", e);
  }
  
  @ResponseStatus(HttpStatus.FORBIDDEN)  // 403
  @ExceptionHandler(ForbiddenException.class)
  @ResponseBody
  public ErrorInformation handleForbidden(ForbiddenException e) {
      return new ErrorInformation("Forbidden", e);
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseBody
  public ErrorInformation handleIllegal(IllegalArgumentException e) {
    return new ErrorInformation("Bad request", e);
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
  @ExceptionHandler(HttpMessageConversionException.class)
  @ResponseBody
  public ErrorInformation handleBadMessageConversion(HttpMessageConversionException e) {
    return new ErrorInformation("Bad request", e);
  }
  
  // Fall back
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInformation handleOtherException(Exception e) {
    return new ErrorInformation("Internal server error", e);
  }
}</pre>
<p style="text-align: justify;">Finally, we can modify the service class to throw exceptions when necessary, as these exceptions will be caught be our global exception handler and return the respective status code and message body.</p>

<h2 style="text-align: justify;">Summary</h2>
<p style="text-align: justify;">In this guide, you will learned the following:</p>

<ul style="text-align: justify;">
 	<li style="text-align: justify;">How to set up and build a simple REST API with Spring.</li>
 	<li style="text-align: justify;">CRUD operations for entries that are saved into a database.</li>
 	<li style="text-align: justify;">How to map HTTP request to specific URL and its response codes.</li>
 	<li style="text-align: justify;">How to handle unmapped requests.</li>
</ul>
<p style="text-align: justify;">Hope you did enjoy it. And please, if you have any question, doubt or comment, do not hesitate and write us.</p>

# Contribute Code
If you would like to become an active contributor to this project please follow theses simple steps:

1. Fork it
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create new Pull Request
