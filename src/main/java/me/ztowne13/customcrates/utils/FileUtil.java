package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.CustomCrates;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class FileUtil
{

	static HashMap<String,FileUtil> map = new HashMap<String,FileUtil>();
	
	String name;
	String directory = "";
	String loaded;
	CustomCrates cc;

	boolean canBeEdited;
	boolean saveWithCustomSave;

	boolean properLoad = false;
	boolean newFile;

	FileConfiguration data = null;
	File dataFile = null;
	
	public FileUtil(CustomCrates cc, String name, String directory, boolean canBeEdited, boolean saveWithCustomSave, boolean newFile)
	{
		this.name = name;
		this.cc = cc;
		this.directory = directory;
		this.canBeEdited = canBeEdited;
		this.saveWithCustomSave = saveWithCustomSave;
		this.newFile = newFile;

		map.put(name, this);
	}
	
	public FileUtil(CustomCrates cc, String name, boolean canBeEdited, boolean saveWithCustomSave)
	{
		this.name = name;
		this.canBeEdited = canBeEdited;
		this.cc = cc;
		this.saveWithCustomSave = saveWithCustomSave;

		map.put(name, this);	
	}
	
	public void reload()
	{
	    if (getDataFile() == null)
	    {
			setDataFile(new File(new File(getCc().getDataFolder().getPath() + getDirectory()), getName()));

	    	if(getName().equalsIgnoreCase("Messages.yml"))
	    	{
	    		if(!folderExists("Crates"))
	    		{
	    			new File(getCc().getDataFolder().getPath() + getDirectory()).mkdir();

	    			try
		    		{
		    			getCc().firstLoadFiles();
		    		}
		    		catch(Exception exc)
		    		{
		    			exc.printStackTrace();
		    		}

	    		}
	    	}
	    }

		try
		{
			data = YamlConfiguration.loadConfiguration(getDataFile());

			if(canBeEdited)
			{
				if (data.saveToString() != null && !data.saveToString().equalsIgnoreCase(""))
				{
                    File defConfigFile = new File(getCc().getDataFolder(), getName());
					if (defConfigFile.exists())
					{
						YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigFile);
						getData().setDefaults(defConfig);
					}


					if (isCanBeEdited())
					{
						loadByByte();
					}

					properLoad = true;
				}
				else
				{
					throw new Exception("");
				}
			}
			else
			{
				properLoad = true;
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			ChatUtils.log(new String[]{"Failed to load the " + name + " file due to a critical error. Please fix the file and restart your server."});
			properLoad = false;
		}
	}

	private void loadByByte()
	{
		if(saveWithCustomSave)
		{
			try
			{
				FileInputStream fileInputSteam = new FileInputStream(getDataFile());

				setLoaded("");

				int content;
				while ((content = fileInputSteam.read()) != -1)
				{
					setLoaded(getLoaded() + ((char) content));
				}

				getData().loadFromString(getLoaded());
			}
			catch (Exception exc)
			{

			}
		}
	}
	
	public boolean folderExists(String path)
	{
		try
		{
			for(File file: getCc().getDataFolder().listFiles())
			{
				if(file.isDirectory() && file.getName().equalsIgnoreCase(path.replace("/", "")))
				{
					return true;
				}
			}
		}
		catch(Exception exc)
		{
			
		}
		return false;
	}

	public void save()
	{
	    if (getData() == null || getDataFile() == null)
	    {
	        return;
	    }
	    
	    try 
	    {
			if(properLoad || newFile)
			{
				if (!isSaveWithCustomSave() || newFile)
				{
					get().save(getDataFile());
				}
				else
				{
					saveByByte();
				}
			}
			else
			{
				ChatUtils.log(name + " file not saving to prevent it from further damage.");
			}
	    }
	    catch (Exception ex)
	    {
			ex.printStackTrace();
	    	getCc().getLogger().log(Level.SEVERE, "Could not save config to " + getDataFile(), ex);
	    }
	}

	private void saveByByte()
	{
		ArrayList<String> bukkitLoad = new ArrayList<>();
		for (String s : getData().saveToString().split("\n"))
		{
			if (!isCommentLine(s))
			{
				bukkitLoad.add(s);
			}
		}

		ArrayList<String> byteLoad = new ArrayList<>(Arrays.asList(getLoaded().split("\n")));

		String modifiedString = "";

		HashMap<String,Integer> lastLevel = new HashMap<String,Integer>();

		for(String s : bukkitLoad)
		{
			String commentSec = "";
			String without = ChatUtils.stripFromWhitespace(s);

			if(!isCommentLine(s))
			{
				String[] split1 = without.split(":");

				int id = 0;
				int currentLevel = 0;
				for(String bks : byteLoad)
				{
					if(ChatUtils.stripFromWhitespace(bks).split(":")[0].equals(split1[0]))
					{
						if(!lastLevel.containsKey(split1[0]) || lastLevel.get(split1[0]) < currentLevel)
						{
							lastLevel.put(split1[0], currentLevel);
							break;
						}
						currentLevel++;
					}
					id++;
				}

				for(int end = id-1; end > -1; end--)
				{
					String line = byteLoad.get(end);
					if(isCommentLine(line))
					{
						commentSec = line + "\n" + commentSec;
					}
					else if(!line.equalsIgnoreCase(s))
					{
						break;
					}
				}
			}
			modifiedString += commentSec + s + "\n";
		}

		try
		{
			FileOutputStream fop = new FileOutputStream(getDataFile());

			byte[] contentInBytes = modifiedString.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public FileConfiguration get()
	{
	    if (getData() == null)
	    {
	        reload();
	    }
	    return getData();
	}

	public void saveDefaults() 
	{
        if(dataFile == null)
        {
            reload();
        }
        
        if(!dataFile.exists())
        {
            cc.saveResource(directory + name, false);
        }
    }

	public boolean isCommentLine(String s)
	{
		return s.trim().length() == 0 || s.equalsIgnoreCase("\n") || ChatUtils.stripFromWhitespace(s).startsWith("#");
	}

	public static void clearLoaded()
	{
		map.clear();
		map = new HashMap<String,FileUtil>();
	}

	public static HashMap<String, FileUtil> getMap()
	{
		return map;
	}

	public static void setMap(HashMap<String, FileUtil> map)
	{
		FileUtil.map = map;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDirectory()
	{
		return directory;
	}

	public void setDirectory(String directory)
	{
		this.directory = directory;
	}

	public String getLoaded()
	{
		return loaded;
	}

	public void setLoaded(String loaded)
	{
		this.loaded = loaded;
	}

	public CustomCrates getCc()
	{
		return cc;
	}

	public void setCc(CustomCrates cc)
	{
		this.cc = cc;
	}

	public FileConfiguration getData()
	{
		return data;
	}

	public void setData(FileConfiguration data)
	{
		this.data = data;
	}

	public File getDataFile()
	{
		return dataFile;
	}

	public void setDataFile(File dataFile)
	{
		this.dataFile = dataFile;
	}

	public boolean isCanBeEdited()
	{
		return canBeEdited;
	}

	public void setCanBeEdited(boolean canBeEdited)
	{
		this.canBeEdited = canBeEdited;
	}

	public boolean isProperLoad()
	{
		return properLoad;
	}

	public void setProperLoad(boolean properLoad)
	{
		this.properLoad = properLoad;
	}

	public boolean isSaveWithCustomSave()
	{
		return saveWithCustomSave;
	}

	public void setSaveWithCustomSave(boolean saveWithCustomSave)
	{
		this.saveWithCustomSave = saveWithCustomSave;
	}
}
