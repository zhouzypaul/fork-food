import { useEffect, useRef, useState } from "react";

const MESSAGE_TYPE = {
    CONNECT: 0,
    UPDATE: 1,
    SEND: 2
};

const useUpdateUsers = (roomId, user) => {
    const [users, setUsers] = useState();
    const socket = useRef(null);
    const id = useRef();

    socket.current = new WebSocket("ws://localhost:4567/socket");

    useEffect(() => {
        socket.current.onmessage = (msg) => {
            //
            const data = JSON.parse(msg.data);
            console.log(data);
            switch (data.type) {
                case MESSAGE_TYPE.CONNECT:
                    id.current = data.payload.id
                    console.log(data)
                    // send a message with our username and room
                    sendInfo()
                    break;
                case MESSAGE_TYPE.UPDATE:
                    // We get this message when some client broadcast a message to all clients
                    // TODO append to the message board unordered list (in chat.ftl) the received message
                    console.log(data.payload.senderMessage)
                    setUsers(data.payload.senderMessage)
                    break;
                default:
                    console.log('Unknown message type!', data.type);
                    break;
            }
        };

        socket.current.onclose = () => {
            //should remove the user
            setUsers()
            console.log("socket closed");
        }

        return () => {
            socket.current.close();
        };
    })

    const sendInfo = () => {
        const message = {
            id: id.current,
            message: {
                roomId: roomId,
                username: user
            }
        };
        socket.current.send(JSON.stringify(message));
    }

    return users;
}

export default useUpdateUsers;