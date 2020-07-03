package jp.co.sample.emp_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Administrator;
import jp.co.sample.emp_management.repository.AdministratorRepository;

/**
 * 管理者情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class AdministratorService {

	@Autowired
	private AdministratorRepository administratorRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 管理者情報を登録します. パスワードはここでハッシュ化されます
	 * 
	 * @param administrator 管理者情報
	 */
	public void insert(Administrator administrator) {

		// パスワードをハッシュ化
		administrator.setPassword(passwordEncoder.encode(administrator.getPassword()));

		administratorRepository.insert(administrator);
	}

	/**
	 * ログインをします.
	 * 
	 * @param mailAddress メールアドレス
	 * @param password    パスワード
	 * @return 管理者情報 存在しない場合またはパスワードが不一致だった場合はnullが返ります
	 */

	public Administrator login(String mailAddress, String passward) {
		Administrator administrator = administratorRepository.findByMailAddress(mailAddress);
		// 対象の管理者がいない場合はnullを返す
		if (administrator == null) {
			return null;
		}
		// パスワードが不一致だった場合はnullを返す
		if (!passwordEncoder.matches(passward, administrator.getPassword())) {
			return null;
		}
		
		return administrator;
	}


	/**
	 * メールアドレスの重複を確認.
	 * 
	 * @param mailAddress メールアドレス
	 * @return 重複していない場合 true、重複している場合 falseが返ります
	 */
	public boolean checkMailAddress(String mailAddress) {
		if (administratorRepository.findByMailAddress(mailAddress) == null) {
			return true;
		} else {
			return false;
		}
	}
}
