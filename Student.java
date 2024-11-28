public class Student {

    int mssv;
    String name;
    double dtb;

    public Student(int mssv, String name, double dtb) {
        if (mssv <= 0) {
            throw new IllegalArgumentException("Mã số sinh viên phải là số dương");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        if (dtb < 0 || dtb > 10) {
            throw new IllegalArgumentException("Điểm trung bình phải từ 0 đến 10");
        }
        this.mssv = mssv;
        this.name = name;
        this.dtb = dtb;
    }

    public int getMssv() {
        return mssv;
    }

    public void setMssv(int mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDtb() {
        return dtb;
    }

    public void setDtb(double dtb) {
        this.dtb = dtb;
    }

    @Override
    public String toString() {
        return String.format("%d - %s - %.2f", mssv, name, dtb);
    }
}
