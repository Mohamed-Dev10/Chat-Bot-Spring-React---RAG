import {AutoCrud} from "@vaadin/hilla-react-crud";
import {PersonService} from "Frontend/generated/endpoints";
import PersonModel from "Frontend/generated/com/lahssini/ragtest/entities/PersonModel";

export default  function Chat() {
    return(
        <AutoCrud service={PersonService}  model={PersonModel}></AutoCrud>
    );
}