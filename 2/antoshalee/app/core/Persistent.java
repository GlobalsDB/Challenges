package core;

import java.util.Date;

public class Persistent {

	public String GetStorageGlobalName() {
		return DataWorker.GetDataGlobalName(this.getClass());
	}

	public String GetIndexGlobalName() {
		return DataWorker.GetIndexGlobalName(this.getClass());
	}

	public long Id = 0;

	@Index(IndexName = "NameIndex")
	public String Name = "";

	@Index(IndexName = "CreatedOnIndex")
	public Date CreatedOn = new Date();

	public String toString() {
		return "Name:" + this.Name + ", Id:" + this.Id + ", CreatedOn:"
				+ CreatedOn;
	}

	public void Save() {
		try {
			DataWorker.Instance().SaveObject(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public Persistent OpenId(Long id) {
		this.Id = id;
		return this.LoadData();

	}

	public Persistent LoadData() {
		Persistent p = null;
		try {
			p = DataWorker.Instance().LoadObjectById(this.Id, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return p;
	}

}
