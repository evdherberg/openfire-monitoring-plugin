/*
 * Copyright (C) 2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.openfire.archive;

import org.jivesoftware.util.cache.ExternalizableUtil;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: gato
 * Date: Oct 9, 2007
 * Time: 11:59:42 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class UserParticipations implements Externalizable {
    /**
     * Flag that indicates if the participations of the user were in a group chat conversation or a one-to-one
     * chat.
     */
    @XmlElement
    private boolean roomParticipation;
    /**
     * Participations of the same user in a groupchat or one-to-one chat. In a group chat conversation
     * a user may leave the conversation and return later so for each time the user joined the room a new
     * participation is going to be created. Moreover, each time the user changes his nickname in the room
     * a new participation is created.
     */
    @XmlElementWrapper
    private List<ConversationParticipation> participations;

    public UserParticipations() {
    }

    public UserParticipations(boolean roomParticipation) {
        this.roomParticipation = roomParticipation;
        if (roomParticipation) {
            participations = new ArrayList<ConversationParticipation>();
        }
        else {
            participations = new CopyOnWriteArrayList<ConversationParticipation>();
        }
    }

    public List<ConversationParticipation> getParticipations() {
        return participations;
    }

    public ConversationParticipation getRecentParticipation() {
        return participations.get(0);
    }

    public void addParticipation(ConversationParticipation participation) {
        participations.add(0, participation);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // ClassCastExceptions occur when using classes provided by a plugin during serialization (sometimes only after
        // reloading the plugin without restarting Openfire. This is why this implementation marshalls data as XML when
        // serializing. See https://github.com/igniterealtime/openfire-monitoring-plugin/issues/120
        // and https://github.com/igniterealtime/openfire-monitoring-plugin/issues/156
        ExternalizableUtil.getInstance().writeBoolean(out, roomParticipation);
        ExternalizableUtil.getInstance().writeInt(out, participations.size());
        for (ConversationParticipation cp :  participations) {
            ExternalizableUtil.getInstance().writeSafeUTF(out, ConversationManager.getXmlSerializer().marshall(cp));
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // ClassCastExceptions occur when using classes provided by a plugin during serialization (sometimes only after
        // reloading the plugin without restarting Openfire. This is why this implementation marshalls data as XML when
        // serializing. See https://github.com/igniterealtime/openfire-monitoring-plugin/issues/120
        // and https://github.com/igniterealtime/openfire-monitoring-plugin/issues/156
        roomParticipation = ExternalizableUtil.getInstance().readBoolean(in);
        if (roomParticipation) {
            participations = new ArrayList<>();
        }
        else {
            participations = new CopyOnWriteArrayList<>();
        }
        int participationCount = ExternalizableUtil.getInstance().readInt(in);
        for (int i = 0; i < participationCount; i++) {
            String marshalledConversationParticipation = ExternalizableUtil.getInstance().readSafeUTF(in);
            final ConversationParticipation unmarshalledParticipation = (ConversationParticipation)ConversationManager.getXmlSerializer().unmarshall(marshalledConversationParticipation);
            participations.add(unmarshalledParticipation);
        }
    }
}
