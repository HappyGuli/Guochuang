package course.netdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import widget.AppException;

public class QuestionSearchedBeanList extends Base{

	private int count;

	private List<QuestionSearchedBean> list = new ArrayList<QuestionSearchedBean>();

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<QuestionSearchedBean> getList() {
		return list;
	}

	public void setList(List<QuestionSearchedBean> list) {
		this.list = list;
	}


	public static QuestionSearchedBeanList parse(JSONArray obj) throws IOException,
			AppException, JSONException {
		QuestionSearchedBeanList beanList = new QuestionSearchedBeanList();
		if (null != obj) {
			beanList.count = obj.length();

			//将json对象转换成为bean对象
			for (int i = 0; i < obj.length(); i++) {
				JSONObject questionJson = obj.getJSONObject(i);
				QuestionSearchedBean question = new QuestionSearchedBean();
				question.setZanNum(Integer.valueOf(questionJson.getString("zanNum")));
				question.setQuestioTitle(questionJson.getString("questioTitle"));
				question.setQuestioTitle(questionJson.getString("questionContent"));
				question.setQid(Integer.valueOf(questionJson.getString("qid")));
				question.setAnsCnt(Integer.valueOf(questionJson.getString("ansCnt")));
				question.setSid(questionJson.getString("sid"));
				question.setUserName(questionJson.getString("userName"));
				beanList.list.add(question);
			}
		}
		return beanList;
	}




}
