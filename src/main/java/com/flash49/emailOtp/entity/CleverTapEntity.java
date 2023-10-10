package com.flash49.emailOtp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "clever_tap")
@Data
public class CleverTapEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "username")
	private String username;
	@Column(name = "password")
	private String password;
	@Column(name = "senderId")
	private String from;
	@Column(name = "dltContentId")
	private String dltContentId;
	@Column(name = "corelationid")
	private String corelationid;
	@Column(name = "phone")
	private String to;
	@Column(name = "text")
	private String text;
	@Column(name = "unicode")
	private String unicode;
	@Column(name = "param")
	private String param;
	@Column(name = "entityid")
	private String entityid;
	@Column(name = "sentDlr")
	private Integer sentDlr;
	@Column(name = "deliverystatus")
	private String deliverystatus;
	@Column(name = "createdDate")
	private String createdDate;
}
