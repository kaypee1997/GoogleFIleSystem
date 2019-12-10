import java.util.List;

public class AppendMessage extends Message {


    private List<Integer> serverList;
    private  List<String> replicaList;

    public List<String> getReplicaList() {
        return replicaList;
    }

    public void setReplicaList(List<String> replicaList) {
        this.replicaList = replicaList;
    }

    public List<Integer> getServerList() {
        return serverList;
    }

    public void setServerList(List<Integer> serverList) {
        this.serverList = serverList;
    }
}
