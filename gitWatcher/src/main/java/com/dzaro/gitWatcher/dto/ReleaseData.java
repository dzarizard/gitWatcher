package com.dzaro.gitWatcher.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "WTC_RELEASES")
public class ReleaseData implements Serializable {

	private static final long serialVersionUID = -4606298164800935652L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RLS_ID")
	private Long id;

	@Column(name = "RLS_USER_NAME")
	private String userName;

	@Column(name = "RLS_PROJECT_NAME")
	private String projectName;

	@Column(name = "RLS_URL")
	private String url;

	@Column(name = "RLS_VERSION", nullable = false)
	private String version;

	@Column(name = "RLS_LAST_UPDATE_DATE")
	private Date updateDate;

	@Column(name = "RLS_LAST_CHECK_DATE")
	private Date checkDate;


	public ReleaseData(String userName, String projectName, String url, String version, Date updateDate, Date checkDate) {
		this.userName = userName;
		this.projectName = projectName;
		this.url = url;
		this.version = version;
		this.updateDate = updateDate;
		this.checkDate = checkDate;
	}

	public ReleaseData() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

}
