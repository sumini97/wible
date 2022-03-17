package com.ssafy.wible.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.wible.config.security.JwtTokenProvider;
import com.ssafy.wible.model.entity.User;
import com.ssafy.wible.model.request.user.SignupRequest;
import com.ssafy.wible.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	public void signup(SignupRequest request){
		request.setPassword(passwordEncoder.encode(request.getPassword()));
		User user = request.toEntity();
		userRepository.save(user);
	}
	
	public boolean emailCheck(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean nicknameCheck(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

	public boolean phoneCheck(String phone) {
		return userRepository.existsByPhone(phone);
	}
	
	public void deleteUser(String email) {
		//해당 유저 관련 좋아요, 리뷰등도 삭제할지
		User user = userRepository.findByEmail(email);
		userRepository.delete(user);
	}
	
	public String login(String email, String password){
		User member = userRepository.findByEmail(email);
		if(member == null) throw new IllegalArgumentException("가입되지 않은 E-MAIL 입니다.");
        if (!passwordEncoder.matches(password, member.getPassword())) throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        
        return jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
    }
	
	public User getUserInfo(int user_seq) {
		return userRepository.findById(user_seq).get();
	}

	public User getUserInfo(String email) {
		return userRepository.findByEmail(email);
	}
	
	public void modify(int seq, String nickname, String phone) {
		User user = userRepository.findById(seq).get();
		user.setNickname(nickname);
		user.setPhone(phone);
		userRepository.save(user);
	}

	public void modify(int seq, String password) {
		User user = userRepository.findById(seq).get();
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
	}
}
