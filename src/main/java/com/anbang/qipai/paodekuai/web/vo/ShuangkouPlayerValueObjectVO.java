package com.anbang.qipai.paodekuai.web.vo;

import java.util.List;

import com.dml.puke.pai.PukePai;
import com.dml.puke.wanfa.dianshu.paizu.DianShuZuPaiZu;
import com.dml.puke.wanfa.position.Position;
import com.dml.paodekuai.player.PaodekuaiPlayerValueObject;
import com.dml.paodekuai.player.action.da.solution.DaPaiDianShuSolution;

public class ShuangkouPlayerValueObjectVO {
	private String id;
	private Position position;
	private ShuangkouPlayerShoupaiVO allShoupai;
	private List<PukePai> liangPaiList;
	private int[] shoupaiDianShuAmountArray;
	private List<List<Integer>> shoupaiIdListForSortList;
	private List<DianShuZuPaiZu> lishiDachuPaiZuList;
	private DianShuZuPaiZu publicDachuPaiZu;
	// private List<DaPaiDianShuSolution> daPaiSolutionsForTips;
	private List<DaPaiDianShuSolution> yaPaiSolutionCandidates;
	private List<DaPaiDianShuSolution> yaPaiSolutionsForTips;
	private boolean guo;
	private boolean watingForMe = false;

	public ShuangkouPlayerValueObjectVO() {

	}

	public ShuangkouPlayerValueObjectVO(PaodekuaiPlayerValueObject shuangkouPlayerValueObject) {
		id = shuangkouPlayerValueObject.getId();
		position = shuangkouPlayerValueObject.getPosition();
		shoupaiIdListForSortList = shuangkouPlayerValueObject.getShoupaiIdListForSortList();
		if (shoupaiIdListForSortList == null || shoupaiIdListForSortList.isEmpty()) {
			allShoupai = new ShuangkouPlayerShoupaiVO(shuangkouPlayerValueObject.getAllShoupai(),
					shuangkouPlayerValueObject.getTotalShoupai(), null);
		} else {
			allShoupai = new ShuangkouPlayerShoupaiVO(shuangkouPlayerValueObject.getAllShoupai(),
					shuangkouPlayerValueObject.getTotalShoupai(), shoupaiIdListForSortList.get(0));
		}
		liangPaiList = shuangkouPlayerValueObject.getLiangPaiList();
		shoupaiDianShuAmountArray = shuangkouPlayerValueObject.getShoupaiDianShuAmountArray();
		lishiDachuPaiZuList = shuangkouPlayerValueObject.getLishiDachuPaiZuList();
		publicDachuPaiZu = shuangkouPlayerValueObject.getPublicDachuPaiZu();
		yaPaiSolutionCandidates = shuangkouPlayerValueObject.getYaPaiSolutionCandidates();
		if (yaPaiSolutionCandidates != null && !yaPaiSolutionCandidates.isEmpty()) {
			watingForMe = true;
		}
		yaPaiSolutionsForTips = shuangkouPlayerValueObject.getYaPaiSolutionsForTips();
		guo = shuangkouPlayerValueObject.isGuo();
	}

	public boolean isWatingForMe() {
		return watingForMe;
	}

	public void setWatingForMe(boolean watingForMe) {
		this.watingForMe = watingForMe;
	}

	public List<PukePai> getLiangPaiList() {
		return liangPaiList;
	}

	public void setLiangPaiList(List<PukePai> liangPaiList) {
		this.liangPaiList = liangPaiList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public ShuangkouPlayerShoupaiVO getAllShoupai() {
		return allShoupai;
	}

	public void setAllShoupai(ShuangkouPlayerShoupaiVO allShoupai) {
		this.allShoupai = allShoupai;
	}

	public int[] getShoupaiDianShuAmountArray() {
		return shoupaiDianShuAmountArray;
	}

	public void setShoupaiDianShuAmountArray(int[] shoupaiDianShuAmountArray) {
		this.shoupaiDianShuAmountArray = shoupaiDianShuAmountArray;
	}

	public List<List<Integer>> getShoupaiIdListForSortList() {
		return shoupaiIdListForSortList;
	}

	public void setShoupaiIdListForSortList(List<List<Integer>> shoupaiIdListForSortList) {
		this.shoupaiIdListForSortList = shoupaiIdListForSortList;
	}

	public List<DianShuZuPaiZu> getLishiDachuPaiZuList() {
		return lishiDachuPaiZuList;
	}

	public void setLishiDachuPaiZuList(List<DianShuZuPaiZu> lishiDachuPaiZuList) {
		this.lishiDachuPaiZuList = lishiDachuPaiZuList;
	}

	public DianShuZuPaiZu getPublicDachuPaiZu() {
		return publicDachuPaiZu;
	}

	public void setPublicDachuPaiZu(DianShuZuPaiZu publicDachuPaiZu) {
		this.publicDachuPaiZu = publicDachuPaiZu;
	}

	public List<DaPaiDianShuSolution> getYaPaiSolutionCandidates() {
		return yaPaiSolutionCandidates;
	}

	public void setYaPaiSolutionCandidates(List<DaPaiDianShuSolution> yaPaiSolutionCandidates) {
		this.yaPaiSolutionCandidates = yaPaiSolutionCandidates;
	}

	public List<DaPaiDianShuSolution> getYaPaiSolutionsForTips() {
		return yaPaiSolutionsForTips;
	}

	public void setYaPaiSolutionsForTips(List<DaPaiDianShuSolution> yaPaiSolutionsForTips) {
		this.yaPaiSolutionsForTips = yaPaiSolutionsForTips;
	}

	public boolean isGuo() {
		return guo;
	}

	public void setGuo(boolean guo) {
		this.guo = guo;
	}

}
