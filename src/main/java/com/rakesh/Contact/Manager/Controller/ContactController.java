package com.rakesh.Contact.Manager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import com.rakesh.Contact.Manager.Entities.Contact;
import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.AppConstants;
import com.rakesh.Contact.Manager.Helpers.Helper;
import com.rakesh.Contact.Manager.Helpers.Message;
import com.rakesh.Contact.Manager.Helpers.MessageType;
import com.rakesh.Contact.Manager.Helpers.PhoneNumberAlreadyExistsException;
import com.rakesh.Contact.Manager.Services.ContactService;
import com.rakesh.Contact.Manager.Services.ImageService;
import com.rakesh.Contact.Manager.Services.UserService;
import com.rakesh.Contact.Mnager.Forms.ContactForm;
import com.rakesh.Contact.Mnager.Forms.ContactSearchForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private ContactService contactService;
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(ContactController.class);
    private ImageService imageService;

    @Autowired
    public ContactController(ContactService contactService, UserService userService, ImageService imageService) {
        this.contactService = contactService;
        this.userService = userService;
        this.imageService = imageService;

    }   

    // Add contact page
    @RequestMapping("/add")
    public String addContact(Model model)
    {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    // Save conatact
    @RequestMapping(value="/saveContact", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult ,Authentication authentication, HttpSession session,Model model)
    {
        // validate contact form inputs
        if(bindingResult.hasErrors())
        {
            logger.info(bindingResult.toString());
            return "user/add_contact";
        }

        // process the form data and save the contact
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        // contact image processing
        String imageUrl = null;
        if(contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty())
        {
            imageUrl = imageService.uploadImage(contactForm.getContactImage());

        }

        Contact contact = Contact.builder()
                            .name(contactForm.getName())
                            .phoneNumber(contactForm.getPhoneNumber())
                            .email(contactForm.getEmail())
                            .address(contactForm.getAddress())
                            .description(contactForm.getDescription())
                            .favorite(contactForm.isFavorite())
                            .instagramLink(contactForm.getInstagramLink())
                            .linkedInLink(contactForm.getLinkedInLink())
                            .picture(imageUrl)
                            .user(user)
                            .build();

        try{
            contactService.save(contact);
        }catch(PhoneNumberAlreadyExistsException exception)
        {
            logger.info("Phone Number already exists");
            model.addAttribute("errormessage", exception.getMessage());
            return "user/add_contact";
        }

        // success message
        Message message = Message.builder().content("Contact saved Successfully !!").type(MessageType.green).build();
        session.setAttribute("message", message);

        return "redirect:/user/contacts/add";
    }

    // view contacts
    @RequestMapping
    public String viewContacts(@RequestParam(value="pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo, 
                                     @RequestParam(value="pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
                                     @RequestParam(value="sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
                                     @RequestParam(value="sortDirection", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDirection,
                                     Authentication authentication, Model model)
    {
        // load all contacts of a user
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Page<Contact> pageContacts = contactService.getByUser(user, pageNo, pageSize, sortBy, sortDirection);
        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // search contacts
    @RequestMapping("/search")
    public String searchContacts(@ModelAttribute ContactSearchForm contactSearchForm,
                                 @RequestParam(value="pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo,
                                @RequestParam(value="pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
                                @RequestParam(value="sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
                                @RequestParam(value="sortDirection", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDirection,
                                Model model, Authentication authentication)
    {
        logger.info("Searching contacts by {} : {}", contactSearchForm.getField(), contactSearchForm.getValue());
        User user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContacts = null;
        if(contactSearchForm.getField().equalsIgnoreCase("name"))
            pageContacts = contactService.searchByName(contactSearchForm.getValue(), pageNo, pageSize, sortBy, sortDirection,user);
        else if(contactSearchForm.getField().equalsIgnoreCase("email"))
            pageContacts = contactService.searchByEmail(contactSearchForm.getValue(), pageNo, pageSize, sortBy, sortDirection,user);
        else if(contactSearchForm.getField().equalsIgnoreCase("phone"))
            pageContacts = contactService.searchByPhoneNumber(contactSearchForm.getValue(), pageNo, pageSize, sortBy, sortDirection,user);
        else
            pageContacts = contactService.getByUser(user, pageNo, pageSize, sortBy, sortDirection);

        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("contactSearchForm",contactSearchForm);

        return "user/search";
    }

    // delete contact
    @RequestMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable String contactId, HttpSession session)
    {
        contactService.delete(contactId);
        Message message = Message.builder().content("Contact deleted Successfully !!").type(MessageType.red).build();
        session.setAttribute("message", message);
        return "redirect:/user/contacts";
    }

    // edit contact page
    @GetMapping("/edit/{contactId}")
    public String editContact(@PathVariable String contactId, Model model)
    {
        Contact contact = contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setInstagramLink(contact.getInstagramLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);
        return "user/update_contact";
    }

    // update contact
    @RequestMapping(value="/updateContact/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable String contactId, @Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, HttpSession session, Model model)
    {
        Contact existingContact = contactService.getById(contactId);
        // validate contact form inputs
        if(bindingResult.hasErrors())
        {
            logger.info(bindingResult.toString());
            return "user/update_contact";
        }
        // contact image processing
        String imageUrl = existingContact.getPicture();
        if(contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty())
        {
            imageUrl = imageService.uploadImage(contactForm.getContactImage());
        }
        existingContact.setName(contactForm.getName());
        existingContact.setEmail(contactForm.getEmail());
        existingContact.setPhoneNumber(contactForm.getPhoneNumber());
        existingContact.setAddress(contactForm.getAddress());
        existingContact.setDescription(contactForm.getDescription());
        existingContact.setFavorite(contactForm.isFavorite());
        existingContact.setInstagramLink(contactForm.getInstagramLink());
        existingContact.setLinkedInLink(contactForm.getLinkedInLink());
        existingContact.setPicture(imageUrl);

        contactService.update(existingContact);

        // success message
        Message message = Message.builder().content("Contact Updated Successfully !!").type(MessageType.green).build();
        session.setAttribute("message", message);


        return "redirect:/user/contacts";
    }

}
