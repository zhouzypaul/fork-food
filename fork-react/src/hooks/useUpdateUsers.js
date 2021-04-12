import { useEffect, useRef, useState } from "react";

const MESSAGE_TYPE = {
    CONNECT: 0,
    UPDATE: 1,


    useEffect(() => {

        const sendInfo = () => {
            const message = {
                id: id.current,
                message: {
                    type: "update_user",
                    roomId: roomId,
                    username: user
                }
            };
            socket.current.send(JSON.stringify(message));
        }
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
                    // check if we're updating users or doing something else
                    if (data.payload.type === "update_user") {
                        setUsers(data.payload.senderMessage.users);
                        console.log("users set");
                    } else if (data.payload.type === "start") {
                        setRestaurants(data.payload.senderMessage.restaurants);
                        console.log("start swiping");
                    }
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

    }, [roomId, user])



    const startSwiping = () => {
        const message = {
            id: id.current,
            message: {
                type: "start",
                roomId: roomId,
                lat: 42.359335,
                lon: -71.059709
            }
        };
        socket.current.send(JSON.stringify(message));
    }
    const sock = socket.current;

    return { users, restaurants, sock, id, startSwiping };
}

export default useUpdateUsers;