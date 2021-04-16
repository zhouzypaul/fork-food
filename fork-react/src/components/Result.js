import TopBar from "./TopBar";
import Stars from "./Stars";
import {useEffect} from "react";
import {Link} from "react-router-dom";

const GOOGLE = "https://www.google.com/search?q=";
const MAP = "https://google.com/maps/search/";

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
        <a href={GOOGLE + searchParameter} target="_blank" rel="noreferrer" className="links">
          <div className="restaurant" id="final-choice">
            <div className="title-text">{rest.name}</div>
            <div><Stars number={rest.numStars}/> from {rest.numReviews} reviews</div>
            <div>{price(rest.priceRange)} &#8226; {rest.city}, {rest.state}</div>
          </div>
        </a>
        <br/>
        <a href={MAP + searchParameter} target="_blank" rel="noreferrer" className="links">
          <div className="location">
            <i className="material-icons-outlined">place</i> <div className="map-text">find on maps</div>
          </div>
        </a>
        <br/>
        <Link to="/home">
          <button className="primary-button">home</button>
        </Link>
      </div>
    </>
  );
}

export default Result;