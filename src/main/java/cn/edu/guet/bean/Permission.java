package cn.edu.guet.bean;

import java.util.Objects;

public class Permission {
    private String id;
    private String pId;
    private String name;
    private String url;
    private String icon;
    private String target;
    private String isParent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIsParent() {
        return isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id='" + id + '\'' +
                ", pId='" + pId + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", icon='" + icon + '\'' +
                ", target='" + target + '\'' +
                ", isParent='" + isParent + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) && Objects.equals(pId, that.pId) && Objects.equals(name, that.name) && Objects.equals(url, that.url) && Objects.equals(icon, that.icon) && Objects.equals(target, that.target) && Objects.equals(isParent, that.isParent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pId, name, url, icon, target, isParent);
    }
}
