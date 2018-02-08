package jjug.speaker;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Speakers implements Iterable<Speaker> {
    private List<Speaker> speakers;

    public Speakers(List<Speaker> speakers) {
        this.speakers = speakers == null ? new ArrayList<>() : speakers;
    }

    public String getName() {
        return this.speakers.stream()
                .map(Speaker::getName)
                .collect(Collectors.joining("／"));
    }

    public String getGithub() {
        return this.speakers.stream()
                .map(Speaker::getGithub)
                .collect(Collectors.joining("／"));
    }

    public String getEmail() {
        return this.speakers.stream()
                .map(Speaker::getEmail)
                .collect(Collectors.joining("／"));
    }

    @Override
    public Iterator<Speaker> iterator() {
        return this.speakers.iterator();
    }
}
