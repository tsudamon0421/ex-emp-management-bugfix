package jp.co.sample.emp_management.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.InsertEmployeeForm;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	@ModelAttribute
	public InsertEmployeeForm setUpInsertForm() {
		InsertEmployeeForm insertEmployeeForm = new InsertEmployeeForm();
		insertEmployeeForm.setGender("女性");
		Date sqlDate = Date.valueOf("2020-04-01");
		insertEmployeeForm.setHireDate(sqlDate);
		
		return insertEmployeeForm;
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/showList")
	public String showList(Model model) {
		List<Employee> employeeList = employeeService.showList();
		model.addAttribute("employeeList", employeeList);
		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員情報の名前のあいまい検索
	/////////////////////////////////////////////////////
	/**
	 * 名前のあいまい検索で従業員情報を取得します.
	 * 
	 * @param name 名前(一部)
	 * @return 従業員情報一覧。空文字の場合は全従業員情報、該当するデータがない場合はメッセージと全従業員情報を返します
	 * 
	 */
	@RequestMapping("/showByName")
	public String showByName(String name, Model model) {
		List<Employee> employeeList = null;
		employeeList = employeeService.showByName(name);
		if (employeeList.size() == 0) {
			model.addAttribute("message", "1件もありませんでした");
			employeeList = employeeService.showList();
		}
		model.addAttribute("employeeList", employeeList);
		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員情報を登録する
	/////////////////////////////////////////////////////
	/**
	 * 従業員情報登録画面にフォワードします.
	 * 
	 * @return 従業員登録画面へフォワード
	 */
	@RequestMapping("/showInsert")
	public String showInsert(Model model) {
		return "employee/insert";
	}

	/**
	 * java.sql.Dateをjava.util.Dateに変換するメソッド.
	 * 
	 * @param sqlDate
	 * @return
	 */
	public java.util.Date convertToUtilDate(java.sql.Date sqlDate) {
		return sqlDate;
	}

	/**
	 * 従業員情報を登録して従業員一覧画面へリダイレクトします.
	 * 
	 * @param form   従業員情報
	 * @param result エラー情報
	 * @param model
	 * @return 従業員一覧画面へリダイレクト
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/insert")
	synchronized String insert(@Validated InsertEmployeeForm form, BindingResult result, Model model)
			throws UnsupportedEncodingException, IOException {
		
		// メールアドレスの重複確認 重複している場合、エラーを作ってresultに格納する
		if (employeeService.checkMailAddress(form.getMailAddress()) == false) {// 重複している場合
			FieldError fieldError = new FieldError(result.getObjectName(), "mailAddress", "このメールアドレスは既に登録されています");
			result.addError(fieldError);
		}
		//画像の未入力チェック
		if(form.getImage() == null) {
			FieldError imageFieldError = new FieldError(result.getObjectName(), "image", "画像がアップロードされていません");
			result.addError(imageFieldError);
		}
		if (result.hasErrors()) {
			return showInsert(model);
		}
		
		// フォームから画像データを取得し、拡張子を判定する
		String name = (form.getImage().getOriginalFilename());
		// System.out.println("ファイルの名前：" + name);
		String extention = name.substring(name.lastIndexOf("."));
		extention = extention.replace(".", "");
		// jpgの場合はjpegに書き換える
		if ("jpg".equals(extention)) {
			extention = "jpeg";
		}
		// System.out.println("拡張子:" + extention);

		// フォームから画像データを取得し、base64にエンコードし、String型にする
		String imageBase64 = new String(Base64.encodeBase64(form.getImage().getBytes()), "ASCII");

		String imageSource = "data:image/" + extention + ";base64," + imageBase64;

		Employee employee = new Employee();
		// フォームからドメインに値をコピー
		BeanUtils.copyProperties(form, employee);
		// コピーできないものを手動でコピー
		employee.setHireDate(convertToUtilDate(form.getHireDate()));
		employee.setSalary(Integer.parseInt(form.getSalary()));
		employee.setImage(imageSource);

		employeeService.insert(employee);

		return "redirect:/employee/showList";

	}

}
