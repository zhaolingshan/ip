import java.io.File;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Encapsulates a storage object with a file path.
 * Loads and saves data onto the hard disk.
 */
public class Storage {
    String filePath;

    /**
     * Instantiates a storage object.
     *
     * @param filePath the path in which the data is saved.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads the data from the hard disk.
     *
     * @return the task list saved in the hard disk.
     * @throws DukeException throws an exception when the file is not found.
     */
    public ArrayList<Task> load() throws DukeException {
        ArrayList<Task> list = new ArrayList<>();
        try {
            File file = new File("taskList.txt");
            if (!file.createNewFile()) { // file already exists
                Scanner fileScanner = new Scanner(file);
                while (fileScanner.hasNextLine()) {
                    String data = fileScanner.nextLine();
                    Task newTask = readFiles(data);
                    list.add(newTask);
                }
                fileScanner.close();
            }
            return list;
        } catch (IOException e) {
            throw new DukeException("ERROR: file not found.");
        }
    }

    /**
     * Saves the updated task list onto the hard disk.
     *
     * @param list the task list to be updated.
     * @throws DukeException throws an exception when the task list fails to be saved.
     */
    public void save(TaskList list) throws DukeException {
        try {
            FileWriter fileWriter = new FileWriter("taskList.txt");
            for (Task task : list.taskList) {
                fileWriter.write(task.saveTask() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            final String ERROR_MESSAGE = "ERROR: failed to save changes.";
            throw new DukeException(ERROR_MESSAGE);
        }
    }

    /**
     * Reads the data from the hard disk.
     *
     * @param data the data to be read from the hard disk.
     * @return the task read from the hard disk.
     * @throws DukeException throws an exception
     *                       when the format of the command is wrong.
     */
    public Task readFiles(String data) throws DukeException {
        assert data.length() > 0 : "input should not be empty";
        final int IS_DONE_INDEX = 4;
        final int START_OF_DESCRIPTION_INDEX = 8;
        Task task;
        if (data.startsWith("T")) {
            task = new Todo(data.substring(START_OF_DESCRIPTION_INDEX));
        } else {
            int index = data.lastIndexOf("|");
            final int DATE_AND_TIME_INDEX = index + 2;
            String description = data.substring(START_OF_DESCRIPTION_INDEX, index - 1);
            String dateString = data.substring(DATE_AND_TIME_INDEX);
            try {
                LocalDate date = LocalDate.parse(dateString);
                if (data.startsWith("E")) {
                    task = new Event(description, date);
                } else if (data.startsWith("D")) {
                    task = new Deadline(description, date);
                } else {
                    final String ERROR_MESSAGE = "ERROR: unknown task";
                    throw new DukeException(ERROR_MESSAGE);
                }
            } catch (DateTimeParseException e) {
                final String ERROR_MESSAGE = "Please key in the date in the format YYYY-MM-DD";
                throw new DukeException(ERROR_MESSAGE);
            }
        }
        if (data.charAt(IS_DONE_INDEX) == '1') {
            task.markAsDone();
        }
        return task;
    }
}