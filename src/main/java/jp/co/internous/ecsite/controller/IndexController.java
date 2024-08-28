package jp.co.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.ecsite.model.domain.MstGoods;
import jp.co.internous.ecsite.model.domain.MstUser;
import jp.co.internous.ecsite.model.dto.HistoryDto;
import jp.co.internous.ecsite.model.form.CartForm;
import jp.co.internous.ecsite.model.form.HistoryForm;
import jp.co.internous.ecsite.model.form.LoginForm;
import jp.co.internous.ecsite.model.mapper.MstGoodsMapper;
import jp.co.internous.ecsite.model.mapper.MstUserMapper;
import jp.co.internous.ecsite.model.mapper.TblPurchaseMapper;

@Controller
@RequestMapping("/ecsite")
public class IndexController {

	@Autowired
	private MstGoodsMapper goodsMapper;
	
	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private TblPurchaseMapper purchaseMapper;
	
	private Gson gson = new Gson();

	// 初期表示	
	@GetMapping("/")
	public String index(Model model) {
		
		// 商品全検索
		List<MstGoods> goods = goodsMapper.findAll();
		model.addAttribute("goods", goods);
		
		return "index";
	}
	
	// ログイン
	@ResponseBody
	@PostMapping("/api/login")
	public String loginApi(@RequestBody LoginForm form) {
		System.out.println("画面入力値ユーザ名:" + form.getUserName());
		System.out.println("画面入力値パスワード:" + form.getPassword());
		
		MstUser user = userMapper.findByUserNameAndPassword(form);
		
		if(user == null) {
			user = new MstUser();
			user.setFullName("ゲスト");
		}
		
		return gson.toJson(user);
	}
	
	// 購入
	@ResponseBody
	@PostMapping("/api/purchase")
	public int purchaseApi(@RequestBody CartForm form) {
		form.getCartList().forEach((c) -> {
			int total = c.getPrice() * c.getCount();
			purchaseMapper.insert(form.getUserId(), c.getId(), c.getGoodsName(), c.getCount(), total);
		});
		return form.getCartList().size();
	}
	
	// 履歴
	@ResponseBody
	@PostMapping("/api/history")
	public String historyApi(@RequestBody HistoryForm form) {
		int userId = form.getUserId();
		List<HistoryDto> history = purchaseMapper.findHistory(userId);
		return gson.toJson(history);
	}
}
