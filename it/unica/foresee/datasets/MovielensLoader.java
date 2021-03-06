package it.unica.foresee.datasets;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Load a Movielens_deprecated dataset from a movielens file.
 */
public class MovielensLoader extends FileDatasetLoader
{
    /**
     * Constructor which initializes the separator to '::'.
     */
    public MovielensLoader()
    {
        super();
        setSeparator("::");
    }

    /**
     * Initializes the object with the given value.
     *
     * @param datasetFile file containing the dataset
     */
    public MovielensLoader(File datasetFile)
    {
        super();
        setSeparator("::");
    }

    /**
     * Initializes the object with the given values.
     *
     * @param separator symbol separating the files
     * @param datasetFile file containing the dataset
     */
    public MovielensLoader(File datasetFile, String separator)
    {
        super(datasetFile, separator);
    }

    /**
     * {@inheritDoc}
     *
     * Loads data from a specified source.
     *
     * A movielens ratings file constains lines in the following form:
     * {@literal UserID::MovieID::Rating::Timestamp}
     * <ul>
     * <li>UserIDs range between 1 and 6040</li>
     * <li>MovieIDs range between 1 and 3952</li>
     * <li>Ratings are made on a 5-star scale (whole-star ratings only, 1 to 5)</li>
     * <li>Timestamp is represented in seconds since the epoch as returned by time(2)</li>
     * </ul>
     * Each user has at least 20 ratings.
     * The Timestamp is discarded, while userID, movieID and rating are stored
     * in the object itself.
     * 
     * @// TODO: 25/03/16 users and movies set could be probably removed
     *
     * @param sourceFile the file from which to load the data
     */
    public Movielens loadDataset(File sourceFile) throws FileNotFoundException
    {
        Movielens dataset = new Movielens();

        // Initial values
        int highestMovieID = 0;

        Scanner dataSource = new Scanner(sourceFile);

        int lineNumber = 1; //follows the line number in the file
        int userID;
        int movieID;
        int rating;

        // Read the file line by line
        while (dataSource.hasNextLine()) {

            Scanner line = new Scanner(dataSource.nextLine());
            line.useDelimiter(getSeparator());

            // Extract a single line of data
            if (line.hasNextInt()) {
                userID = line.nextInt();
            } else {
                throw new InputMismatchException("expected userID at line " + lineNumber);
            }

            if (line.hasNextInt()) {
                movieID = line.nextInt();
                if (movieID > highestMovieID)
                {
                    highestMovieID = movieID;
                }
            } else {
                throw new InputMismatchException("expected movieID at line " + lineNumber);
            }

            if (line.hasNextInt()) {
                rating = line.nextInt();
            } else {
                throw new InputMismatchException("expected rating at line " + lineNumber);
            }

            // Check the correctness of the data
            if (userID < 1) {
                throw new InputMismatchException("userID < 1 at line " + lineNumber);
            }

            if (movieID < 1) {
                throw new InputMismatchException("movieID < 1 at line " + lineNumber);
            }

            if (rating < 1) {
                throw new InputMismatchException("rating < 1 at line " + lineNumber);
            }

            dataset.put(userID, movieID, (double) rating);

            lineNumber++;
        }

        if (dataset.getUsersAmount() > lineNumber)
        {
            throw new IllegalStateException("The amount of users is higher than entries.");
        }

        if (dataset.getMoviesAmount() > lineNumber)
        {
            throw new IllegalStateException("The amount of movies is higher than entries.");
        }

        if (dataset.getUsersAmount() < 20)
        {
            throw new IllegalStateException("The amount of users is lower than 20.");
        }

        // Set the vector size for every element
        dataset.setInternalVectorSize(highestMovieID);
        for (MovielensElement el : dataset.values())
        {
            el.setVectorSize(highestMovieID);
        }

        return dataset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Movielens loadDataset() throws FileNotFoundException
    {
        return loadDataset(this.getDatasetFile());
    }
}
