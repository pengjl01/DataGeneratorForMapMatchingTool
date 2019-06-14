package tools;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

/*
 * @author pjl
 * @version 创建时间：2019年3月26日 下午12:52:14
 * 类说明
 */
public class TaxiDAO {
	/*
	 * 查询区域内某时段所有轨迹，并排序
	 */
	public static SearchResponse taxiESSearchAreaTime(double top, double left, double bottom, double right,
			String starttime, String endtime) {
		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
				new HttpHost(Constants.ES_SERVER_ADDRESS, Constants.ES_SERVER_PORT, Constants.ES_SERVER_SCHEME),
				new HttpHost(Constants.ES_SERVER_ADDRESS, Constants.ES_SERVER_PORT1, Constants.ES_SERVER_SCHEME)));
		try {
			RangeQueryBuilder rqb = QueryBuilders.rangeQuery(Constants.TAXI_TIME).gte(starttime).lte(endtime);
			GeoBoundingBoxQueryBuilder geoqb = QueryBuilders.geoBoundingBoxQuery(Constants.TAXI_LOCATION)
					.setCorners(top, left, bottom, right);
			QueryBuilder qb = QueryBuilders.boolQuery().must(geoqb).must(rqb);
			String[] includeFields = new String[] { Constants.TAXI_ID, Constants.TAXI_LOCATION,
					Constants.TAXI_DIRECTION, Constants.TAXI_TIME, Constants.TAXI_SPEED };
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(qb).size(10000)
//					.timeout(new TimeValue(Constants.TIMEOUT, TimeUnit.SECONDS))
					.fetchSource(includeFields, null).sort(Constants.TAXI_TIME, SortOrder.ASC);
			SearchRequest searchRequest = new SearchRequest(Constants.TAXI);
			searchRequest.source(sourceBuilder);
			return client.search(searchRequest);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * 查询某辆车的某时段轨迹
	 */
	public static MultiSearchResponse taxiESSearchByTimeMulti(List<String> uids, String starttime, String endtime) {
		MultiSearchRequest searchRequest = new MultiSearchRequest();
		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
				new HttpHost(Constants.ES_SERVER_ADDRESS, Constants.ES_SERVER_PORT, Constants.ES_SERVER_SCHEME),
				new HttpHost(Constants.ES_SERVER_ADDRESS, Constants.ES_SERVER_PORT1, Constants.ES_SERVER_SCHEME)));
		for (String uid : uids) {
			searchRequest.add(taxiPathBuildRequest(uid, starttime, endtime));
		}
		try {
			return client.multiSearch(searchRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * 查询某辆车的某时段轨迹
	 */
	public static SearchResponse taxiESSearchByTime(String uid, String starttime, String endtime) {
		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
				new HttpHost(Constants.ES_SERVER_ADDRESS, Constants.ES_SERVER_PORT, Constants.ES_SERVER_SCHEME),
				new HttpHost(Constants.ES_SERVER_ADDRESS, Constants.ES_SERVER_PORT1, Constants.ES_SERVER_SCHEME)));
		try {
			return client.search(taxiPathBuildRequest(uid, starttime, endtime));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	// taxipath构建查询
	private static SearchRequest taxiPathBuildRequest(String uid, String starttime, String endtime) {
		try {
			RangeQueryBuilder rqb = QueryBuilders.rangeQuery(Constants.TAXI_TIME).gte(starttime).lte(endtime);
			MatchQueryBuilder mqb = QueryBuilders.matchQuery(Constants.TAXI_ID, uid);
			QueryBuilder qb = QueryBuilders.boolQuery().must(mqb).must(rqb);
			String[] includeFields = new String[] { Constants.TAXI_LOCATION, Constants.TAXI_DIRECTION,
					Constants.TAXI_TIME, Constants.TAXI_ID };
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(qb).size(10000)
					.fetchSource(includeFields, null).sort(Constants.TAXI_TIME, SortOrder.ASC);
			SearchRequest searchRequest = new SearchRequest(Constants.TAXI);
			searchRequest.source(sourceBuilder);
			return searchRequest;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
