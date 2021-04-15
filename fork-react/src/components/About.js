import TopBar from "./TopBar";
import {useEffect} from "react";

function About() {

  return (
    <>
      <TopBar to="/" showOptions={false}/>
      <div className="text">
        <h1 className="h1">what is fork?</h1>
        <p>
          Fork helps you make decisions in those fork-in-the-road moments. Simply go with a group or go solo. Fork will recommend nearby
          restaurants based on the group's preferences. All you have to do is swipe and fork will pick the group's favorite restaurant. No more
          indecisiveness just more eating and enjoying.
        </p>
        <h1>the team</h1>
        <ul>
          <li>Edward Xing - big brain, bigger heart</li>
          <li>Alan Gu - the lover</li>
          <li>Paul Zhou - the algos guy</li>
          <li>Sean Zhan - puts the P in OOP</li>
        </ul>
      </div>
    </>
  );
}

export default About;