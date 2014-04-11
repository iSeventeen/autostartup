import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.autostartup.dao.StudentDao;
import com.android.autostartup.model.Student;

public class TestStudentDao extends AndroidTestCase {

    StudentDao studentDao = new StudentDao(getContext());

    @Override
    protected void setUp() throws Exception {
        studentDao = new StudentDao(getContext());
    }

    public void testSave() {
        for (int i = 0; i < 10; i++) {
            studentDao.save(new Student(-1, "123456" + i, "Alex" + i, 18, 0, "url"));
        }
    }
    
    public void testFindById() {
        Student student = studentDao.findById(1);
        Log.i("tag", student.toString()); 
    }
    
    public void testFindAll() {  
        List<Student> students = studentDao.getAll();  
        for (Student student : students) {  
            Log.i("tag", student.toString());  
        }  
    } 

}
