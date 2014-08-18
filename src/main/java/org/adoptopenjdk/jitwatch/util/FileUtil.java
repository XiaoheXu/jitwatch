package org.adoptopenjdk.jitwatch.util;

import static org.adoptopenjdk.jitwatch.core.JITWatchConstants.S_BACKSLASH;
import static org.adoptopenjdk.jitwatch.core.JITWatchConstants.S_DOT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtil
{
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil()
	{
	}

	public static void copyFilesToDir(File sourceDir, File targetDir)
	{
		if (sourceDir.exists() && sourceDir.isDirectory())
		{
			File[] sourceFiles = sourceDir.listFiles();
			
			logger.debug("Copying {} files", sourceFiles.length);

			for (File exampleFile : sourceFiles)
			{
				try
				{
					Path srcPath = exampleFile.toPath();
					Path dstPath = targetDir.toPath().resolve(exampleFile.getName());

					logger.debug("Copying file {} -> {}", srcPath, dstPath);
					
					Files.copy(srcPath, dstPath);
				}
				catch (IOException ioe)
				{
					logger.error("Could not copy {} to {}", exampleFile, targetDir, ioe);
				}
			}
		}
		else
		{
			logger.error("Could not find source directory {}", sourceDir);
		}
	}

	public static File writeSource(File sourceDir, String fqClassName, String sourceCode) throws IOException
	{
		String[] parts = fqClassName.split(S_BACKSLASH + S_DOT);

		StringBuilder builder = new StringBuilder();

		builder.append(sourceDir.getAbsolutePath()).append(File.separatorChar);

		for (String part : parts)
		{
			builder.append(part).append(File.separatorChar);
		}

		builder.deleteCharAt(builder.length() - 1);

		builder.append(".java");

		String filePathString = builder.toString();

		int lastSep = filePathString.lastIndexOf(File.separatorChar);

		File sourceFile;

		if (lastSep != -1)
		{
			String dirPart = filePathString.substring(0, lastSep);
			String filePart = filePathString.substring(lastSep + 1);
			File dir = new File(dirPart);

			if (!dir.exists())
			{
				dir.mkdirs();
			}

			sourceFile = new File(dir, filePart);
		}
		else
		{
			sourceFile = new File(filePathString);
		}

		logger.info("Writing source file: {}", sourceFile.getAbsolutePath());

		BufferedWriter fout = new BufferedWriter(new FileWriter(sourceFile));

		try
		{
			fout.write(sourceCode);
			fout.flush();
		}
		finally
		{
			fout.close();
		}

		return sourceFile;
	}

	public static void emptyDir(File directory)
	{
		if (directory.exists() && directory.isDirectory())
		{
			File[] contents = directory.listFiles();

			for (File file : contents)
			{
				if (file.isDirectory())
				{
					emptyDir(file);
					file.delete();
				}
				else
				{
					file.delete();
				}
			}
		}
	}
}
