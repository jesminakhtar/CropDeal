package com.cropdeal.usermanagement.service;

import com.cropdeal.usermanagement.dto.EmailDetails;

public interface EmailService {

	String sendSimpleMail(EmailDetails details);

	String sendMailWithAttachment(EmailDetails details);
}