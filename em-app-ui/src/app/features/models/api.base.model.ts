export interface BaseDto {

}

export interface BaseHistDto extends BaseDto {
  createdBy?: String;
  createdDate?: Date;
  updatedBy?: String;
  updatedDate?: Date;
}