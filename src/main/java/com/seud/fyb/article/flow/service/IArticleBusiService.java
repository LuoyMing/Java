package com.seud.fyb.article.flow.service;

import java.util.Map;

public interface IArticleBusiService {

	Map<String, Object> start(Map<String, Object> params);

	Map<String, Object> audit(Map<String, Object> params);

}
