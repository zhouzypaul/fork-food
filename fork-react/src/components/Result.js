import TopBar from "./TopBar";
import Stars from "./Stars";
import {useEffect} from "react";

function Result(props) {
  const rest = props.location.resultProps.result;
  console.log(rest)

  useEffect(() => {
    window.onbeforeunload = () => {
      return true;
    };
    return () => {
      window.onbeforeunload = null;
    }
  })

  const price = (p) => {
    let ret = "";
    for (let i = 0; i < p; i++) {
      ret += "$";
    }
    return ret;
  }

  const searchParameter = rest.name.replace(/\s/g, "+") + "+" + rest.city + "+" + rest.state;

  return (
    <>
      <TopBar to="/home" showOptions={true}/>
      <div className="content">
        <div className="title-text">head to</div>
        <a href={"https://www.google.com/search?q=" + searchParameter} target="_blank" className="links">
          <div className="restaurant" id="final-choice">
            <div className="title-text">{rest.name}</div>
            <div><Stars number={rest.numStars}/> from {rest.numReviews} reviews</div>
            <div>{price(rest.priceRange)} &#8226; {rest.city}, {rest.state}</div>
          </div>
        </a>
        <br/>
        <a href={"https://google.com/maps/search/" + searchParameter} target="_blank" className="links">
          <i className="material-icons-outlined">place</i> find on maps
        </a>
      </div>
    </>
  );
}

export default Result;