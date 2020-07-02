package jp.co.sample.emp_management.form;


import java.sql.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 従業員登録用のフォームクラス.
 * 
 * @author hikaru.tsuda
 *
 */
public class InsertEmployeeForm {

	/** 従業員名 */
	@NotBlank(message="氏名の入力は必須です")
	private String name;
	/** 画像 */
	@NotBlank(message="画像の添付は必須です")
	private String image;
	/** 性別 */
	private String gender;
	/** 入社日 */
	private Date hireDate;
	/** メールアドレス */
	@NotBlank(message="メールアドレスの入力は必須です")
	@Email(message="メールアドレスの形式が不正です")
	private String mailAddress;
	/** 郵便番号 */
	@NotBlank(message="郵便番号の入力は必須です")
	private String zipCode;
	/** 住所 */
	@NotBlank(message="住所の入力は必須です")
	private String address;
	/** 電話番号 */
	@NotBlank(message="電話番号の入力は必須です")
	private String telephone;
	/** 給料 */
	@NotBlank(message="給料の入力は必須です")
	private String salary;
	/** 特性 */
	@NotBlank(message="特性の入力は必須です")
	private String characteristics;
	/** 扶養人数 */
	private Integer dependentsCount;

	@Override
	public String toString() {
		return "InsertEmployeeForm [name=" + name + ", image=" + image + ", gender=" + gender + ", hireDate=" + hireDate
				+ ", mailAddress=" + mailAddress + ", zipCode=" + zipCode + ", address=" + address + ", telephone="
				+ telephone + ", salary=" + salary + ", characteristics=" + characteristics + ", dependentsCount="
				+ dependentsCount + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(String characteristics) {
		this.characteristics = characteristics;
	}

	public Integer getDependentsCount() {
		return dependentsCount;
	}

	public void setDependentsCount(Integer dependentsCount) {
		this.dependentsCount = dependentsCount;
	}

}
