package it.unica.jpc;

import it.unica.jpc.parsers.Parser;
import static it.unica.jpc.utils.Tools.err;
import static it.unica.jpc.utils.Tools.log;
import static it.unica.jpc.utils.Tools.warn;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Main class to run the JPC framework.
 */
public class JPC
{

    /**
     * Main function to load the program.
     * @param args command line parameters.
     *             Possible arguments are:
     *             <ul>
     *              <li>-h --help: Shows help message</li>
     *              <li>-p --path: Path to the instruction file folder</li>
     *              <li>-i --interactive: Interactive Mode</li>
     *             </ul>
     *             Options:
     *             <ul>
     *              <li>-v --verbose: Enable verbose output</li>
     *             </ul>
     */
    public static void main (String[] args)
    {
        /*
         * Special selections:
         *  -   0: no selection
         *  - "!": invalid argument
         */
        boolean verbose;

        /* Flag to enable verbose output */
        verbose = false;

        /* Argument selected */
        char selection = 0;

        /* Options */
        String instructionsPath = new String();

        /* Error message */
        String errorMessage;

        /* Exit status */
        int exitStatus = 0;

        /* The parser for the instructions */
        Parser p = null;

        /* Parsing loop */
        for (int i = 0; i < args.length; i++)
        {
            /* Help */
            if (args[i].equals("-h")  || args[i].equals("--help"))
            {
                if (selection == 0)
                {
                    selection = 'h';
                }
                else
                {
                    selection = '!';
                }
            }/* Interactive */
            else if (args[i].equals("-i")  || args[i].equals("--interactive"))
            {
                if (selection == 0)
                {
                    selection = 'i';
                }
                else
                {
                    selection = '!';
                }
            }/* Path */
            else if (args[i].equals("-p")  || args[i].equals("--path"))
            {
                if (selection == 0)
                {
                    selection = 'p';

                    try
                    {
                        i++;
                        instructionsPath = args[i];
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        err("option -p, --path requires <path>");
                        help();
                        System.exit(1);
                    }
                }
                else
                {
                    selection = '!';
                }
            }/* Verbose */
            else if (args[i].equals("-v")  || args[i].equals("--verbose"))
            {
                if (verbose == false)
                {
                    verbose = true;
                    log("Enabling verbose mode: brace yourself");
                }
                else
                {
                    selection = '!';
                }
            }/* No matching argument */
            else
            {
                selection = '!';
            }
        }

        /* Execution */
        switch (selection)
        {
            /* Help */
            case 'h':
                help();
                break;

            /* Interactive mode */
            case 'i':
                if(verbose){log("Loading interactive mode...");}
                try
                {
                    p = new Parser(verbose);
                    exitStatus = p.parse();
                }
                catch (FileNotFoundException e)
                {
                    err(e.getMessage());
                    exitStatus = 1;
                }
                break;

            /* Path: load instruction files at <path> */
            case 'p':
                if(verbose){log("Loading file " + instructionsPath + "...");}
                try
                {
                    File instructionsFile = new File(instructionsPath);
                    p = new Parser(instructionsFile, verbose);
                    exitStatus = p.parse();
                }
                catch (FileNotFoundException e)
                {
                    err("file not found: " + instructionsPath);
                    exitStatus = 1;
                }
                break;

            /* No option specified */
            case 0:
                err("no argument specified");
                exitStatus = 1;
                break;

            /* Wrong option specified */
            case '!':
                err("incorrect argument specified");
                exitStatus = 1;
                break;

        }
        System.exit(exitStatus);
    }

    /**
     * Prints a useful help message.
     */
    public static void help()
    {
        System.out.println("" +
                "JPC - Java Predict and Cluster" +
                "\nA software for clustering and data mining by the University of Cagliari.");

        /* Display usage*/
        System.out.println("" +
                "Usage: jpc -iv|-h|-pv <path>" +
                "\n" +
                "\n-i, --interactive \tRun in interactive mode." +
                "\n-h, --help        \tShow this help message." +
                "\n-p, --path <path> \tUse <path> as the path where to find instruction files." +
                "\n\nOptions:" +
                "\n-v, --verbose     \tEnable verbose output.");
    }
}