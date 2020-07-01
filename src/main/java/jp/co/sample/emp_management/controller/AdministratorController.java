package jp.co.sample.emp_management.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sample.emp_management.domain.Administrator;
import jp.co.sample.emp_management.form.InsertAdministratorForm;
import jp.co.sample.emp_management.form.LoginForm;
import jp.co.sample.emp_management.service.AdministratorService;

/**
 * 管理者情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/")
public class AdministratorController {

	@Autowired
	private AdministratorService administratorService;

	@Autowired
	private HttpSession session;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public InsertAdministratorForm setUpInsertAdministratorForm() {
		return new InsertAdministratorForm();
	}

	// (SpringSecurityに任せるためコメントアウトしました)
	@ModelAttribute
	public LoginForm setUpLoginForm() {
		return new LoginForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：管理者を登録する
	/////////////////////////////////////////////////////
	/**
	 * 管理者登録画面を出力します.
	 * 
	 * @return 管理者登録画面
	 */
	@RequestMapping("/toInsert")
	public String toInsert(Model model) {
		return "administrator/insert";
	}

	/**
	 * 管理者情報を登録します.
	 * 
	 * @param form 管理者情報用フォーム
	 * @return ログイン画面へリダイレクト
	 */
	@RequestMapping("/insert")
	public String insert(@Validated InsertAdministratorForm form, BindingResult result, Model model) {
		Administrator administrator = new Administrator();
		// もしパスワードが一致しない場合、エラーを作ってresultに格納する
		if (!form.getConPassword().equals(form.getPassword())) {
			FieldError fieldError = new FieldError(result.getObjectName(), "conPassword", "パスワードが一致しません");
			result.addError(fieldError);
		}
		// メールアドレスの重複確認 重複している場合、エラーを作ってresultに格納する
		if (administratorService.checkMailAddress(form.getMailAddress()) == false) {// 重複している場合
			FieldError fieldError = new FieldError(result.getObjectName(), "mailAddress", "このメールアドレスは既に登録されています");
			result.addError(fieldError);
		}
		// エラーが１つでも存在する場合、エラー情報を渡してログイン画面にリダイレクト
		if (result.hasErrors()) {
			return toInsert(model);
		}
		// エラーが存在しない場合、フォームからドメインにプロパティ値をコピー
		BeanUtils.copyProperties(form, administrator);
		administratorService.insert(administrator);
		return "redirect:/";
	}

	/////////////////////////////////////////////////////
	// ユースケース：ログインをする
	/////////////////////////////////////////////////////
	/**
	 * ログイン画面を出力します.
	 * 
	 * @return ログイン画面
	 */
	@RequestMapping("/")
	public String toLogin() {
		return "administrator/login";
	}

	/**
	 * ログインします.
	 * 
	 * @param form   管理者情報用フォーム
	 * @param result エラー情報格納用オブッジェクト
	 * @return ログイン後の従業員一覧画面
	 */
	@RequestMapping("/login")
	public String login(LoginForm form, BindingResult result, Model model) {
		Administrator administrator = administratorService.login(form.getMailAddress(), form.getPassword());
		if (administrator == null) {
			model.addAttribute("errorMessage", "メールアドレスまたはパスワードが不正です。");
			return toLogin();
		}
		//セッションスコープにページで表示するための名前を格納
		session.setAttribute("Administratorname", administrator.getName());
		return "forward:/employee/showList";
	}

	/////////////////////////////////////////////////////
	// ユースケース：ログアウトをする
	/////////////////////////////////////////////////////
	/**
	 * ログアウトをします. (SpringSecurityに任せるためコメントアウトしました)
	 * 
	 * @return ログイン画面
	 */
	@RequestMapping(value = "/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/";
	}
	
	/**
	 *505エラーを出すテストのためのメソッド.
	 * ここで発生した例外はGlobalExceptionHandlerが捕獲し処理をします
	 * 
	 * @throws ArithmeticException このメソッドは必ずArithmeticExceptionを発生します
	 */
	@RequestMapping("/exception")
	public String throwsException() {
		// 0で除算、非検査例外であるArithmeticExceptionが発生！
		System.out.println("例外発生前");
		System.out.println(10 / 0); // ←このタイミングでGlobalExceptionHandlerに処理が飛ぶ
		System.out.println("例外発生後");

		return "通常はここにHTML名を書くが、ここまで処理は来ない";
	}


}
