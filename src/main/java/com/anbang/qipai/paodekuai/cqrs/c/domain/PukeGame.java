package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anbang.qipai.paodekuai.cqrs.c.domain.listener.BoomCountDaActionStatisticsListener;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PukeActionResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiJuResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanPlayerResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanResult;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.extend.fpmpv.FixedPlayersMultipanAndVotetofinishGame;
import com.dml.mpgame.game.extend.multipan.WaitingNextPan;
import com.dml.mpgame.game.extend.vote.VoteNotPassWhenPlaying;
import com.dml.mpgame.game.player.GamePlayer;
import com.dml.mpgame.game.player.PlayerPlaying;
import com.dml.paodekuai.exception.BiyaException;
import com.dml.paodekuai.pai.dianshuzu.comparator.DaipaiComparator;
import com.dml.paodekuai.pai.dianshuzu.comparator.TongDengDaiPaiComparator;
import com.dml.paodekuai.preparedapai.fapai.PaodekuaiFapaiStrategy;
import com.dml.paodekuai.preparedapai.luanpai.PaodekuaiLuanpaiStrategy;
import com.dml.paodekuai.preparedapai.zhuaniao.HongxinshiPlayerDeterminer;
import com.dml.paodekuai.wanfa.OptionalPlay;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.DanGeDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.LianXuDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.NoZhadanDanGeDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.TongDengLianXuDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.ZhadanComparator;
import com.dml.paodekuai.gameprocess.FixedPanNumbersJuFinishiDeterminer;
import com.dml.paodekuai.gameprocess.OnePlayerNoPaiPanFinishiDeterminer;
import com.dml.paodekuai.ju.Ju;
import com.dml.paodekuai.pai.waihao.PaodekuaiWaihaoGenerator;
import com.dml.paodekuai.pan.Pan;
import com.dml.paodekuai.pan.PanActionFrame;
import com.dml.paodekuai.preparedapai.avaliablepai.CutTypeAvaliablePaiFiller;
import com.dml.paodekuai.preparedapai.lipai.DianshuOrPaishuShoupaiSortStrategy;
import com.dml.paodekuai.preparedapai.xianda.HeitaosanDaPlayerDeterminer;

public class PukeGame extends FixedPlayersMultipanAndVotetofinishGame {
	private int panshu;
	private int renshu;
    private OptionalPlay optionalPlay;
	private Ju ju;
	private Map<String, Integer> playerTotalScoreMap = new HashMap<>();

	private void createJuAndStartFirstPan(long currentTime) throws Exception {
		ju = new Ju();
		// 局盘结束
		ju.setCurrentPanFinishiDeterminer(new OnePlayerNoPaiPanFinishiDeterminer());
		ju.setJuFinishiDeterminer(new FixedPanNumbersJuFinishiDeterminer(panshu));

		// 生成盘结果
		PaodekuaiCurrentPanResultBuilder panResultBuilder = new PaodekuaiCurrentPanResultBuilder();
		panResultBuilder.setRenshu(renshu);
		panResultBuilder.setOptionalPlay(optionalPlay);
		ju.setCurrentPanResultBuilder(panResultBuilder);

		// 生成局结果
		ju.setJuResultBuilder(new PaodekuaiJuResultBuilder());

		// 可用牌、乱牌、理牌、鸟牌、发牌、先打牌
		ju.setAvaliablePaiFiller(new CutTypeAvaliablePaiFiller());
		ju.setLuanpaiStrategyForFirstPan(new PaodekuaiLuanpaiStrategy());
		ju.setLuanpaiStrategyForNextPan(new PaodekuaiLuanpaiStrategy());
		ju.setShoupaiSortStrategy(new DianshuOrPaishuShoupaiSortStrategy());
		ju.setZhuaniaoPlayerDeterminer(new HongxinshiPlayerDeterminer(optionalPlay.isZhuaniao()));
		ju.setFapaiStrategyForFirstPan(new PaodekuaiFapaiStrategy());
		ju.setFapaiStrategyForNextPan(new PaodekuaiFapaiStrategy());
		ju.setXiandaPlayerDeterminer(new HeitaosanDaPlayerDeterminer(optionalPlay.isBichu()));

		// 牌组比较
		DanGeDianShuZuComparator danGeDianShuZuComparator = new NoZhadanDanGeDianShuZuComparator();
		LianXuDianShuZuComparator lianXuDianShuZuComparator = new TongDengLianXuDianShuZuComparator();
		DaipaiComparator daipaiComparator = new TongDengDaiPaiComparator();
		ZhadanComparator zhadanComparator = new PaodekuaiZhadanComparator();

		// 提示器
		PaodekuaiYaPaiSolutionsTipsFilter paodekuaiYaPaiSolutionsTipsFilter = new PaodekuaiYaPaiSolutionsTipsFilter();
		paodekuaiYaPaiSolutionsTipsFilter.setZhadanComparator(zhadanComparator);
		paodekuaiYaPaiSolutionsTipsFilter.setOptionalPlay(optionalPlay);
		ju.setYaPaiSolutionsTipsFilter(paodekuaiYaPaiSolutionsTipsFilter);

		// 可打牌
		PaodekuaiAllKedaPaiSolutionsGenerator paodekuaiAllKedaPaiSolutionsGenerator = new PaodekuaiAllKedaPaiSolutionsGenerator();
		paodekuaiAllKedaPaiSolutionsGenerator.setOptionalPlay(optionalPlay);
		paodekuaiAllKedaPaiSolutionsGenerator.setLianXuDianShuZuComparator(lianXuDianShuZuComparator);
		paodekuaiAllKedaPaiSolutionsGenerator.setZhadanComparator(zhadanComparator);
		paodekuaiAllKedaPaiSolutionsGenerator.setDaipaiComparator(daipaiComparator);
		ju.setAllKedaPaiSolutionsGenerator(paodekuaiAllKedaPaiSolutionsGenerator);

		// 外号
		ju.setWaihaoGenerator(new PaodekuaiWaihaoGenerator());

		// 点数组压牌比较
		PaodekuaiDianShuZuYaPaiSolutionCalculator dianShuZuYaPaiSolutionCalculator = new PaodekuaiDianShuZuYaPaiSolutionCalculator();
		dianShuZuYaPaiSolutionCalculator.setOptionalPlay(optionalPlay);
		dianShuZuYaPaiSolutionCalculator.setDanGeDianShuZuComparator(danGeDianShuZuComparator);
		dianShuZuYaPaiSolutionCalculator.setLianXuDianShuZuComparator(lianXuDianShuZuComparator);
		dianShuZuYaPaiSolutionCalculator.setDaipaiComparator(daipaiComparator);
		ju.setDianShuZuYaPaiSolutionCalculator(dianShuZuYaPaiSolutionCalculator);

		// 炸弹压牌比较
		PaodekuaiZaDanYaPaiSolutionCalculator zaDanYaPaiSolutionCalculator = new PaodekuaiZaDanYaPaiSolutionCalculator();
		zaDanYaPaiSolutionCalculator.setOptionalPlay(optionalPlay);
		zaDanYaPaiSolutionCalculator.setZhadanComparator(zhadanComparator);
		ju.setZaDanYaPaiSolutionCalculator(zaDanYaPaiSolutionCalculator);

		// 已打出的炸弹
		BoomCountDaActionStatisticsListener boomListener = new BoomCountDaActionStatisticsListener();
		for (String list : allPlayerIds()) {
			boomListener.getPlayerzhadanshuMap().put(list, 0);
		}
		ju.addDaListener(new BoomCountDaActionStatisticsListener());

		// 开始第一盘
		ju.startFirstPan(allPlayerIds(), currentTime);
	}

	public PanActionFrame findFirstPanActionFrame() {
		return ju.getCurrentPan().findLatestActionFrame();
	}

	public PukeActionResult da(String playerId, List<Integer> paiIds, String dianshuZuheIdx, long actionTime)
			throws Exception {
		PanActionFrame panActionFrame = null;

		panActionFrame = ju.da(playerId, paiIds, dianshuZuheIdx, actionTime);

		PukeActionResult result = new PukeActionResult();
		result.setPanActionFrame(panActionFrame);

		if (state.name().equals(VoteNotPassWhenPlaying.name)) {
			state = new Playing();
		}

		checkAndFinishPan();

		if (state.name().equals(WaitingNextPan.name) || state.name().equals(Finished.name)) {// 盘结束了
			PaodekuaiPanResult panResult = (PaodekuaiPanResult) ju.findLatestFinishedPanResult();
			for (PaodekuaiPanPlayerResult paodekuaiPanPlayerResult : panResult.getPanPlayerResultList()) {
				playerTotalScoreMap.put(paodekuaiPanPlayerResult.getPlayerId(),
						paodekuaiPanPlayerResult.getTotalScore());
			}

			result.setPanResult(panResult);
			if (state.name().equals(Finished.name)) {// 局结束了
				result.setJuResult((PaodekuaiJuResult) ju.getJuResult());
			}
		} else {
//			// 计算胜负分
//			List<String> playerIds = allPlayerIds();
//			Pan currentPan = ju.getCurrentPan();
//			allPlayerIds().forEach((pid) -> {
//			});
		}
		result.setPukeGame(new PukeGameValueObject(this));
		return result;
	}

	public PukeActionResult guo(String playerId, long actionTime) throws Exception {
		if (optionalPlay.isBiya() && ju.canPlay(playerId)) {
			throw new BiyaException();
		}

		PanActionFrame panActionFrame = null;
		panActionFrame = ju.guo(playerId, actionTime);

		PukeActionResult result = new PukeActionResult();
		result.setPanActionFrame(panActionFrame);
		if (state.name().equals(VoteNotPassWhenPlaying.name)) {
			state = new Playing();
		}
		result.setPukeGame(new PukeGameValueObject(this));
		return result;
	}

	@Override
	protected boolean checkToFinishGame() throws Exception {
		return ju.getJuResult() != null;
	}

	@Override
	protected boolean checkToFinishCurrentPan() throws Exception {
		return ju.getCurrentPan() == null;
	}

	@Override
	protected void startNextPan() throws Exception {
		ju.startNextPan();
		PaodekuaiCurrentPanResultBuilder panResultBuilder = (PaodekuaiCurrentPanResultBuilder) ju
				.getCurrentPanResultBuilder();

		state = new Playing();
		updateAllPlayersState(new PlayerPlaying());

	}

	@SuppressWarnings("unchecked")
	@Override
	public PukeGameValueObject toValueObject() {
		return new PukeGameValueObject(this);
	}

	@Override
	public void start(long currentTime) throws Exception {
		createJuAndStartFirstPan(currentTime);

		state = new Playing();
		updateAllPlayersState(new PlayerPlaying());
	}

	@Override
	public void finish() throws Exception {
		if (ju != null) {
			ju.finish();
		}
	}

	/**
	 * ----------------------- 拓展玩法（跑的快不需要）
	 */

	@Override
	protected void updatePlayerToExtendedVotingState(GamePlayer player) {
//		if (player.getState().name().equals(PlayerChaodi.name)) {
//			player.setState(new PlayerVotingWhenChaodi());
//		} else if (player.getState().name().equals(PlayerAfterChaodi.name)) {
//			player.setState(new PlayerVotingWhenAfterChaodi());
//		}
	}

	@Override
	protected void updateToExtendedVotingState() {
//		if (state.name().equals(StartChaodi.name) || state.name().equals(VoteNotPassWhenChaodi.name)) {
//			state = new VotingWhenChaodi();
//		}
	}

	@Override
	protected void updatePlayerToExtendedVotedState(GamePlayer player) {
//		String stateName = player.getState().name();
//		if (stateName.equals(PlayerVotingWhenChaodi.name)) {
//			player.setState(new PlayerVotedWhenChaodi());
//		} else if (player.getState().name().equals(PlayerVotingWhenAfterChaodi.name)) {
//			player.setState(new PlayerVotedWhenAfterChaodi());
//		}
	}

	@Override
	protected void recoveryPlayersStateFromExtendedVoting() throws Exception {
//		if (state.name().equals(VoteNotPassWhenChaodi.name)) {
//			for (GamePlayer player : idPlayerMap.values()) {
//				if (player.getState().name().equals(PlayerVotingWhenChaodi.name)
//						|| player.getState().name().equals(PlayerVotedWhenChaodi.name)) {
//					updatePlayerState(player.getId(), new PlayerChaodi());
//				} else if (player.getState().name().equals(PlayerVotingWhenAfterChaodi.name)
//						|| player.getState().name().equals(PlayerVotedWhenAfterChaodi.name)) {
//					updatePlayerState(player.getId(), new PlayerAfterChaodi());
//				}
//			}
//		}
	}

	@Override
	protected void updateToVoteNotPassStateFromExtendedVoting() throws Exception {
//		if (state.name().equals(VotingWhenChaodi.name)) {
//			state = new VoteNotPassWhenChaodi();
//		}
	}

	/**
	 * -----------------set/get
	 */

	public int getPanshu() {
		return panshu;
	}

	public void setPanshu(int panshu) {
		this.panshu = panshu;
	}

	public int getRenshu() {
		return renshu;
	}

	public void setRenshu(int renshu) {
		this.renshu = renshu;
	}

	public OptionalPlay getOptionalPlay() {
		return optionalPlay;
	}

	public void setOptionalPlay(OptionalPlay optionalPlay) {
		this.optionalPlay = optionalPlay;
	}

	public Ju getJu() {
		return ju;
	}

	public void setJu(Ju ju) {
		this.ju = ju;
	}

	public Map<String, Integer> getPlayerTotalScoreMap() {
		return playerTotalScoreMap;
	}

	public void setPlayerTotalScoreMap(Map<String, Integer> playerTotalScoreMap) {
		this.playerTotalScoreMap = playerTotalScoreMap;
	}

}
