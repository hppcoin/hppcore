package org.hppcoin.dao;

import java.util.List;

import org.hppcoin.model.LMNode;

public interface LMNodeDao {
	LMNode save(LMNode node);

	LMNode getWinner();

	void update(List<LMNode> nodes);

	List<LMNode> getAll();

	LMNode getWinnerGeo();

}
