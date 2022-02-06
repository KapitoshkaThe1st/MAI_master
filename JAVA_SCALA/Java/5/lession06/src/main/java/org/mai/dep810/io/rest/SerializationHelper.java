package org.mai.dep810.io.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.*;

public class SerializationHelper<T extends Serializable> {

    Class<T> serialazationType;

    public SerializationHelper(Class<T> serialazationType) {
        this.serialazationType = serialazationType;
    }

    private Logger log = Logger.getLogger(getClass());

    ObjectMapper mapper = new ObjectMapper();

    /*
      Необходимо десереализовать объект из файла по указанному пути
     */
    public T loadFromFile(String path) {
        T result = null;
        try(ObjectInputStream istream = new ObjectInputStream(new FileInputStream(path))){
            result = serialazationType.cast(istream.readObject());
        }
        catch (Exception ex){
            log.error("Error while deserializing object", ex);
        }
        return result;
    }

    /*
      Необходимо сохранить сереализованный объект в файл по указанному пути
     */
    public boolean saveToFile(String path, T toSave) {
        boolean succeeded = true;
        try(ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(path))){
            ostream.writeObject(toSave);
        }
        catch (Exception ex){
            log.error("Error while serializing object", ex);
            succeeded = false;
        }
        return succeeded;
    }

    public String convertToJsonString(T toConvert) {
        try {
            String json = mapper.writeValueAsString(toConvert);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeJsonToStream(OutputStream output, T toWrite) {
        try {
            mapper.writeValue(output, toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T parseJson(String json) {
        try {
            return mapper.readValue(json, serialazationType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
