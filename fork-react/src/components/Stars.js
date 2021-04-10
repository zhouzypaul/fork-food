import {useEffect, useState} from "react";

function Stars(props) {
  const [stars, setStars] = useState("");

  useEffect(() => {
    const s = props.number;
    let ret = "";
    for (let i = 0; i < 5; i++) {
      let type = "\u2606"
      if (i < s) {
        type = "\u2605"
      }
      if (i === 0) {
        ret += type;
      } else {
        ret += " " + type;
      }
    }
    setStars(ret);
  }, []);

  return (
    <span className="stars">
      {stars}
    </span>
  );
}

export default Stars;